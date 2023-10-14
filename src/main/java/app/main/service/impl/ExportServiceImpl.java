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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
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
import org.springframework.stereotype.Component;

import static app.main.converter.ConvertToDTO.convertListToSectionDto;
import static app.main.converter.ConvertToDTO.convertToAsyncJobDTO;

@Component
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

    public ExportServiceImpl(JobRepository jobRepository,
                             SectionRepository sectionRepository,
                             JobToSectionRepository jobToSectionRepository) {
        this.jobRepository = jobRepository;
        this.sectionRepository = sectionRepository;
        this.jobToSectionRepository = jobToSectionRepository;
    }

    @Override
    public CompletableFuture<AsyncJobDTO> triggerExport() {
        CompletableFuture<AsyncJobDTO> future = new CompletableFuture<>();

        Job job = new Job();
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartTime(LocalDateTime.now());
        job.setJobType("EXPORT");
        jobRepository.save(job);

        CompletableFuture.runAsync(() -> {
            try (HSSFWorkbook workbook = new HSSFWorkbook()) {
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

                saveFile(job, workbook);
                job.setStatus(JobStatus.DONE);
            } catch (IOException e) {
                job.setStatus(JobStatus.ERROR);
                logger.warning("Something went wrong while parsing to excel file, with error message: " + e.getMessage());
            } finally {
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);
            }
        }, threadPool);

        var endTime = Objects.nonNull(job.getEndTime()) ? job.getEndTime() : null;
        future.complete(convertToAsyncJobDTO(job, endTime));
        return future;
    }

    private void saveFile(Job job, HSSFWorkbook workbook) throws IOException {
        String fileName = generateFileName("xls", job.getId());
        String filePath = fileStorageDirectory + File.separator + fileName;
        File file = new File(filePath);

        if (file.getParentFile().mkdirs() && file.createNewFile()){
            logger.info("File Created " + fileName);
        } else {
            logger.warning("File " + fileName +" already exists");
        }

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
                try {
                    resource = getFileAsResource(String.valueOf(job.getId()));
                } catch (IOException e) {
                    throw new FileException("Unable to parse data to file, error message: " + e.getMessage());
                }
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


    private Resource getFileAsResource(String jobId) throws IOException {
        Path uploadDirectory = Paths.get(fileStorageDirectory);
        AtomicReference<Path> foundFile = new AtomicReference<>();
        Files.list(uploadDirectory).forEach(file -> {
            if (file.getFileName().toString().startsWith(jobId)) {
                foundFile.set(file);
            }
        });

        if (foundFile.get() != null) {
            return new UrlResource(foundFile.get().toUri());
        }

        return null;
    }

    public String generateFileName(String fileExtension, Long jobId) {
        return jobId + "-export." + fileExtension;
    }
}
