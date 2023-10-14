package app.main.controller;

import app.main.api.ExportEndpoint;
import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import app.main.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletionStage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExportController implements ExportEndpoint {
    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @Override
    public CompletionStage<AsyncJobDTO> triggerExport() {
        return exportService.triggerExport();
    }

    @Override
    public CompletionStage<ParsingResultDTO> getExportStatus(Long jobId) {
        return exportService.getExportStatus(jobId);
    }

    @Override
    public CompletionStage<ResponseEntity<?>> downloadFile(Long jobId) {
        return exportService.downloadFile(jobId);
    }
}
