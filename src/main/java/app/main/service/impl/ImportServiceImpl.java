package app.main.service.impl;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Job;
import app.main.entity.JobToSection;
import app.main.entity.Section;
import app.main.entity.enums.JobStatus;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.JobRepository;
import app.main.repository.JobToSectionRepository;
import app.main.repository.SectionRepository;
import app.main.service.ImportService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static app.main.converter.ConvertToDTO.convertListToSectionDto;
import static app.main.converter.ConvertToDTO.convertToAsyncJobDTO;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final JobRepository jobRepository;
    private final SectionRepository sectionRepository;
    private final GeologicalClassRepository geologicalClassRepository;
    private final JobToSectionRepository jobToSectionRepository;

    private final ThreadFactory threadFactory =
            new ThreadFactoryBuilder().setDaemon(true).build();
    private final Executor threadPool =
            new ThreadPoolExecutor(6, 20, 3000,
                    TimeUnit.SECONDS, new LinkedBlockingDeque<>(200), threadFactory);

    private final Logger logger = Logger.getLogger(ImportServiceImpl.class.getName());

    @Override
    public CompletableFuture<AsyncJobDTO> parseXslAndReturnJobId(MultipartFile file) {
        CompletableFuture<AsyncJobDTO> future = new CompletableFuture<>();

        Job job = new Job();
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartTime(LocalDateTime.now());
        job.setJobType("IMPORT");
        jobRepository.save(job);

        startImportAsync(file, job);

        future.complete(convertToAsyncJobDTO(job, null));
        return future;
    }

    private void startImportAsync(MultipartFile file, Job job) {
        CompletableFuture.runAsync(() -> {
            try (InputStream inputStream = file.getInputStream()) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                parseAndSaveRows(sheet, job);
                job.setStatus(JobStatus.DONE);
            } catch (IOException e) {
                job.setStatus(JobStatus.ERROR);
                logger.log(Level.WARNING, "ImportServiceImpl class threw error {}", e.getMessage());
            } finally {
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);
            }
        }, threadPool);
    }

    @Override
    public CompletableFuture<ParsingResultDTO> getImportStatus(Long jobId) {
        return CompletableFuture.supplyAsync(() ->
            sectionRepository.findAllByJobType(jobId)
        ).thenApply(sectionList -> {
            Job job = jobRepository.getJobById(jobId);
            var endTime = Objects.nonNull(job.getEndTime()) ? job.getEndTime() : null;
            return new ParsingResultDTO(convertListToSectionDto(sectionList), convertToAsyncJobDTO(job, endTime));
        });
    }

    private void parseAndSaveRows(Sheet sheet, Job job) {
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }
            Section section = null;
            for (Cell cell : row) {
                int cellIndex = cell.getColumnIndex();
                if (cellIndex == 0) {
                    // First cell in a row is the Section name
                    section = new Section();
                    section.setName(cell.getStringCellValue());
                    sectionRepository.save(section);
                } else if (cellIndex % 2 != 0) {
                    // Odd cell index indicates a GeologicalClass name
                    GeologicalClass geologicalClass = new GeologicalClass();
                    geologicalClass.setName(cell.getStringCellValue());

                    // Get the next cell for the GeologicalClass code
                    Cell codeCell = row.getCell(cellIndex + 1);
                    if (codeCell != null) {
                        geologicalClass.setCode(codeCell.getStringCellValue());
                    }

                    if (section != null) {
                        geologicalClass.setSection(section);
                    }
                    geologicalClassRepository.save(geologicalClass);
                    jobToSectionRepository.save(new JobToSection(section, job));
                }
            }
        }
    }
}
