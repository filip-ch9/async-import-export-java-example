package app.main.api;

import app.main.dto.SectionDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.concurrent.CompletionStage;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("sections")
public interface SectionEndpoint {

    @Operation(summary = "Create section", description = "Create section with provided params")
    @PostMapping("/create")
    CompletionStage<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO);

    @Operation(summary = "Get section", description = "Get section by provided id")
    @GetMapping("/{section-id}/fetch")
    CompletionStage<SectionDTO> fetchSection(@PathVariable("section-id") Long sectionId);

    @Operation(summary = "Get all sections", description = "Get sections by the provided code")
    @GetMapping("/by-code")
    CompletionStage<List<SectionDTO>> fetchSectionsByGeologicalClassCode(@RequestParam("code") String code);
    @Operation(summary = "Update section", description = "Update section with provided params")
    @PutMapping("/{section-id}/update")
    CompletionStage<SectionDTO> updateSection(@PathVariable("section-id") Long sectionId, @RequestBody SectionDTO sectionDTO);

    @Operation(summary = "Delete section", description = "Delete section by provided id")
    @DeleteMapping("/{section-id}/delete")
    CompletionStage<Boolean> deleteSection(@PathVariable("section-id") Long sectionId);
}
