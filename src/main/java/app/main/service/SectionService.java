package app.main.service;

import app.main.dto.SectionDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public interface SectionService {

    CompletableFuture<SectionDTO> getSection(Long id);

    CompletableFuture<List<SectionDTO>> getAllSectionsByGeologicalCode(String code);

    CompletableFuture<SectionDTO> saveSection(SectionDTO sectionDTO);

    CompletableFuture<SectionDTO> updateSection(Long sectionId, SectionDTO sectionDTO);

    CompletableFuture<Boolean> deleteSection(Long sectionId);
}
