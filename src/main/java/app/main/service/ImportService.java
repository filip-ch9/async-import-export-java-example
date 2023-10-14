package app.main.service;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImportService {

    CompletableFuture<AsyncJobDTO> parseXslAndReturnJobId(MultipartFile file);

    CompletableFuture<ParsingResultDTO> getImportStatus(Long jobId);
}
