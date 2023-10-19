package app.main.service.impl;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Job;
import app.main.entity.JobToSection;
import app.main.entity.Section;
import app.main.entity.enums.JobStatus;
import app.main.exception.ExportException;
import app.main.exception.FileException;
import app.main.repository.JobRepository;
import app.main.repository.JobToSectionRepository;
import app.main.repository.SectionRepository;
import app.main.service.ExportService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.main.converter.ConvertToDTO.convertListToSectionDto;
import static app.main.converter.ConvertToDTO.convertToAsyncJobDTO;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    @Value("${exported.file.directory}")
    private String fileStorageDirectory;
    private final JobRepository jobRepository;
    private final SectionRepository sectionRepository;
    private final JobToSectionRepository jobToSectionRepository;

    private final ThreadFactory threadFactory =
            new ThreadFactoryBuilder().setDaemon(true).build();
    private final Executor threadPool =
            new ThreadPoolExecutor(6, 20, 3000,
                    TimeUnit.SECONDS, new LinkedBlockingDeque<>(200), threadFactory);
    private final Logger logger = Logger.getLogger(ExportServiceImpl.class.getName());

    @Override
    public CompletableFuture<AsyncJobDTO> triggerExport() {
        CompletableFuture<AsyncJobDTO> future = new CompletableFuture<>();

        Job job = new Job();
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartTime(LocalDateTime.now());
        job.setJobType("EXPORT");
        jobRepository.save(job);

        startExportAsync(job);

        future.complete(convertToAsyncJobDTO(job, null));
        return future;
    }

    private void startExportAsync(Job job) {
        CompletableFuture.runAsync(() -> {
            try (HSSFWorkbook workbook = new HSSFWorkbook()) {
                fillExcelSheet(job, workbook);

                saveFile(job, workbook);
                job.setStatus(JobStatus.DONE);
            } catch (IOException e) {
                job.setStatus(JobStatus.ERROR);
                logger.log(Level.WARNING, "Something went wrong while parsing to excel file, with error message: {}", e.getMessage());
            } finally {
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);
            }
        }, threadPool);
    }

    private void fillExcelSheet(Job job, HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("Sections");
        List<Section> sections = sectionRepository.findAll();
        // Create the header row
        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Section Name");
        headerRow.createCell(1).setCellValue("Geological Class Name");
        headerRow.createCell(2).setCellValue("Geological Class Code");

        int rowIndex = 1; // Start from row 1 after the header

        // Iterate through sections and their geological classes
        for (Section section : sections) {
            String sectionName = section.getName();
            for (GeologicalClass geologicalClass : section.getGeologicalClasses()) {
                HSSFRow dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(sectionName);
                dataRow.createCell(1).setCellValue(geologicalClass.getName());
                dataRow.createCell(2).setCellValue(geologicalClass.getCode());
            }
            jobToSectionRepository.save(new JobToSection(section, job));
        }
    }

    private void fillExcelSheetDynamicColumns(Job job, HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("Sections");
        List<Section> sections = sectionRepository.findAll();

        // Create the header row
        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Section Name");
        int maxColumnCount = sections.stream()
                .mapToInt(section -> section.getGeologicalClasses().size() * 2)
                .max()
                .orElse(1); // Initialize to 1 for Section Name

        for (int i = 1; i <= maxColumnCount; i++) {
            headerRow.createCell(i).setCellValue("Geological Class Name");
            i++;
            headerRow.createCell(i).setCellValue("Geological Class Code");
        }

        int rowIndex = 1; // Start from row 1 after the header

        // Iterate through sections and their geological classes
        for (Section section : sections) {
            String sectionName = section.getName();
            HSSFRow dataRow = sheet.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(sectionName);

            int cellIndex = 1; // Start from column 1 for geological class data

            for (GeologicalClass geologicalClass : section.getGeologicalClasses()) {
                dataRow.createCell(cellIndex++).setCellValue(geologicalClass.getName());
                dataRow.createCell(cellIndex++).setCellValue(geologicalClass.getCode());
            }

            // Fill remaining columns with blank cells if necessary
            while (cellIndex <= maxColumnCount) {
                dataRow.createCell(cellIndex++).setCellValue(""); // Blank cell
                dataRow.createCell(cellIndex++).setCellValue(""); // Blank cell
            }

            rowIndex++;
            jobToSectionRepository.save(new JobToSection(section, job));
        }
    }

    private void saveFile(Job job, HSSFWorkbook workbook) throws IOException {
        String fileName = generateFileName("xls", job.getId());
        String filePath = fileStorageDirectory + File.separator + fileName;
        File file = new File(filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
        workbook.write(fileOutputStream);
    }

    @Override
    public CompletableFuture<ParsingResultDTO> getExportStatus(Long jobId) {
        return CompletableFuture.supplyAsync(() ->
                sectionRepository.findAllByJobType(jobId)
        ).thenApply(sectionList -> {
            Job job = jobRepository.getJobById(jobId);
            var endTime = Objects.nonNull(job.getEndTime()) ? job.getEndTime() : null;
            return new ParsingResultDTO(convertListToSectionDto(sectionList), convertToAsyncJobDTO(job, endTime));
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<?>> downloadFile(Long jobId) {
        String filePath = fileStorageDirectory + File.separator + jobId + ".xls";
       return CompletableFuture.supplyAsync(() -> {
            Resource resource = null;
            Job job = jobRepository.getJobById(jobId);
            if (job.getStatus() == JobStatus.DONE) {
                resource = getFileAsResource(String.valueOf(job.getId()));
                if (resource == null) {
                    return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
                }
            } else if (job.getStatus() == JobStatus.IN_PROGRESS) {
                throw new ExportException("Export is not finished");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filePath.substring(filePath.lastIndexOf("/") + 1))
                    .body(resource);
        });
    }

    private Resource getFileAsResource(String jobId) {
        try (Stream<Path> files = Files.list(Paths.get(fileStorageDirectory))) {
            Optional<Path> foundFile = files
                    .filter(file -> file.getFileName().toString().startsWith(jobId))
                    .findFirst();

            return foundFile.map(file -> {
                try {
                    return new UrlResource(file.toUri());
                } catch (MalformedURLException e) {
                    throw new FileException("Error creating URL for the file, with error message: " + e.getMessage());
                }
            }).orElse(null);
        } catch (IOException e) {
            throw new FileException("Something went wrong while getting the file, with error message: " + e.getMessage());
        }
    }

    public String generateFileName(String fileExtension, Long jobId) {
        return jobId + "-export." + fileExtension;
    }
}
