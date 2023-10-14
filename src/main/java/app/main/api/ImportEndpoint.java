package app.main.api;

import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.CompletionStage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("import")
public interface ImportEndpoint {

    @Operation(summary = "Import file", description = "Import file async and return async job DTO")
    @PostMapping("/file")
    CompletionStage<AsyncJobDTO> importFileAsync(@RequestBody() MultipartFile file);

    @Operation(summary = "Import status", description = "Get import status by provided async job DTO")
    @GetMapping("/{id}/status")
    CompletionStage<ParsingResultDTO> getImportStatus(@PathVariable("id") Long jobId);
}
