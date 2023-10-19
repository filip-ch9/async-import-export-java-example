package app.main.service;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;

public interface ExportService {

    CompletableFuture<AsyncJobDTO> triggerExport();

    CompletableFuture<ParsingResultDTO> getExportStatus(Long jobId);

    CompletableFuture<ResponseEntity<?>> downloadFile(Long jobId);
}
