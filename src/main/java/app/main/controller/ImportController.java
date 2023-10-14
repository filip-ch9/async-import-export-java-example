package app.main.controller;

import app.main.api.ImportEndpoint;
import app.main.dto.AsyncJobDTO;
import app.main.dto.ParsingResultDTO;
import app.main.service.ImportService;
import java.util.concurrent.CompletionStage;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImportController implements ImportEndpoint {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @Override
    public CompletionStage<AsyncJobDTO> importFileAsync(MultipartFile file) {
        return importService.parseXslAndReturnJobId(file);
    }

    @Override
    public CompletionStage<ParsingResultDTO> getImportStatus(Long jobId) {
        return importService.getImportStatus(jobId);
    }
}
