package app.main.controller;

import app.main.dto.SectionDTO;
import app.main.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionControllerTest {

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private SectionController sectionController;

    @Test
    void testCreateSection() {
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setName("TestSection");

        when(sectionService.saveSection(sectionDTO))
                .thenReturn(CompletableFuture.completedFuture(sectionDTO));

        CompletableFuture<SectionDTO> result = sectionController.createSection(sectionDTO)
                .toCompletableFuture();
        assertEquals(sectionDTO, result.join());
        verify(sectionService, Mockito.times(1)).saveSection(sectionDTO);
    }

    @Test
    void testFetchSection() {
        Long sectionId = 1L;
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setName("TestSection");

        when(sectionService.getSection(sectionId))
                .thenReturn(CompletableFuture.completedFuture(sectionDTO));

        CompletableFuture<SectionDTO> result = sectionController.fetchSection(sectionId)
                .toCompletableFuture();
        assertEquals(sectionDTO, result.join());
        verify(sectionService, Mockito.times(1)).getSection(sectionId);
    }

    @Test
    void testFetchSectionsByGeologicalClassCode() {
        String geologicalCode = "TestCode";
        List<SectionDTO> sectionDTOList = List.of(new SectionDTO(), new SectionDTO());

        when(sectionService.getAllSectionsByGeologicalCode(geologicalCode))
                .thenReturn(CompletableFuture.completedFuture(sectionDTOList));

        CompletableFuture<List<SectionDTO>> result = sectionController.fetchSectionsByGeologicalClassCode(geologicalCode)
                .toCompletableFuture();
        assertEquals(sectionDTOList, result.join());
        verify(sectionService, Mockito.times(1)).getAllSectionsByGeologicalCode(geologicalCode);
    }

    @Test
    void testUpdateSection() {
        Long sectionId = 1L;
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setName("UpdatedSection");

        when(sectionService.updateSection(sectionId, sectionDTO))
                .thenReturn(CompletableFuture.completedFuture(sectionDTO));

        CompletableFuture<SectionDTO> result = sectionController.updateSection(sectionId, sectionDTO)
                .toCompletableFuture();
        assertEquals(sectionDTO, result.join());
        verify(sectionService, Mockito.times(1)).updateSection(sectionId, sectionDTO);
    }

    @Test
    void testDeleteSection() {
        Long sectionId = 1L;

        when(sectionService.deleteSection(sectionId))
                .thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> result = sectionController.deleteSection(sectionId).toCompletableFuture();
        assertTrue(result.join());
        verify(sectionService, Mockito.times(1)).deleteSection(sectionId);
    }
}
