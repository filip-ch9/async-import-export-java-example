package app.main.api;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.CompletionStage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("export")
public interface ExportEndpoint {

    @Operation(summary = "Start file export", description = "Trigger export process and return async job DTO")
    @GetMapping()
    CompletionStage<AsyncJobDTO> triggerExport();

    @Operation(summary = "Export status", description = "Get export status by provided async job DTO")
    @GetMapping("/{id}/status")
    CompletionStage<ParsingResultDTO> getExportStatus(@PathVariable("id") Long jobId);

    @Operation(summary = "Download excel file", description = "Download the excel file by the job id")
    @GetMapping("/{job-id}/download")
    CompletionStage<ResponseEntity<?>> downloadFile(@PathVariable("job-id") Long jobId);
}
