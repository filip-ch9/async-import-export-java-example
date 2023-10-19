package app.main.controller;

import app.main.api.SectionEndpoint;
import app.main.dto.SectionDTO;
import app.main.service.SectionService;
import java.util.List;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SectionController implements SectionEndpoint {

    private final SectionService sectionService;

    @Override
    public CompletionStage<SectionDTO> createSection(SectionDTO sectionDTO) {
        return sectionService.saveSection(sectionDTO);
    }

    @Override
    public CompletionStage<SectionDTO> fetchSection(Long sectionId) {
        return sectionService.getSection(sectionId);
    }

    @Override
    public CompletionStage<List<SectionDTO>> fetchSectionsByGeologicalClassCode(String code) {
        return sectionService.getAllSectionsByGeologicalCode(code);
    }

    @Override
    public CompletionStage<SectionDTO> updateSection(Long sectionId, SectionDTO sectionDTO) {
        return sectionService.updateSection(sectionId, sectionDTO);
    }

    @Override
    public CompletionStage<Boolean> deleteSection(Long sectionId) {
        return sectionService.deleteSection(sectionId);
    }
}
