package app.main.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import app.main.entity.Job;
import app.main.entity.Section;
import app.main.entity.enums.JobStatus;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.JobRepository;
import app.main.repository.JobToSectionRepository;
import app.main.repository.SectionRepository;
import app.main.service.impl.ImportServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @InjectMocks
    private ImportServiceImpl importService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GeologicalClassRepository geologicalClassRepository;

    @Mock
    private JobToSectionRepository jobToSectionRepository;

    @Test
    void testParseXslAndReturnJobId() {
        MultipartFile file = mock(MultipartFile.class);
        Job job = new Job();
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setJobType("IMPORT");
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        CompletableFuture<AsyncJobDTO> result = importService.parseXslAndReturnJobId(file);

        assertNotNull(result);
        job.setStartTime(result.join().getStartTime());

        assertEquals(JobStatus.IN_PROGRESS, job.getStatus());
        verify(jobRepository, times(1)).save(job);
    }


    @Test
    void testGetImportStatus() {
        Long jobId = 1L;
        List<Section> sectionList = new ArrayList<>(); // Mock section list
        Job job = new Job();
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setJobType("IMPORT");
        when(sectionRepository.findAllByJobType(jobId)).thenReturn(sectionList);
        when(jobRepository.getJobById(jobId)).thenReturn(job);

        CompletableFuture<ParsingResultDTO> result = importService.getImportStatus(jobId);

        assertNotNull(result);
        verify(sectionRepository, Mockito.times(1)).findAllByJobType(jobId);
        verify(jobRepository, Mockito.times(1)).getJobById(jobId);
    }

    // TODO Additional test cases to verify the parsing of the excel

}
