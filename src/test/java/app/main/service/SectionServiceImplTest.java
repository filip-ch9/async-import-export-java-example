package app.main.service;

import app.main.dto.GeologicalClassDTO;
import app.main.dto.SectionDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Section;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.SectionRepository;
import app.main.service.impl.SectionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GeologicalClassRepository geologicalClassRepository;

    @InjectMocks
    private SectionServiceImpl sectionService;

    @Test
    void testGetSection() {
        Long sectionId = 1L;
        Section section = new Section();
        section.setId(sectionId);
        section.setName("ExpectedSectionName");
        GeologicalClass geologicalClass = new GeologicalClass();
        section.setGeologicalClasses(List.of(geologicalClass));

        when(sectionRepository.getSectionsById(sectionId)).thenReturn(section);

        CompletableFuture<SectionDTO> result = sectionService.getSection(sectionId);
        assertNotNull(result);

        assertEquals("ExpectedSectionName", result.join().getName());
        verify(sectionRepository, Mockito.times(1)).getSectionsById(sectionId);
    }

    @Test
    void testGetAllSectionsByGeologicalCode() {
        String geologicalCode = "TestCode";
        List<Section> sections = new ArrayList<>();
        when(sectionRepository.findAllByGeologicalClasses(geologicalCode)).thenReturn(sections);

        CompletableFuture<List<SectionDTO> > result = sectionService.getAllSectionsByGeologicalCode(geologicalCode);
        assertNotNull(result);
        assertTrue(result.join().isEmpty());

        verify(sectionRepository, Mockito.times(1)).findAllByGeologicalClasses(geologicalCode);
    }

    @Test
    void testSaveSection() {
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setName("TestSection");
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        sectionDTO.setGeologicalClassList(List.of(geologicalClassDTO));

        Section section = new Section();
        section.setName("TestSection");

        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        CompletableFuture<SectionDTO> result = sectionService.saveSection(sectionDTO);
        assertNotNull(result);
        assertEquals(sectionDTO.getName(), result.join().getName());

        verify(sectionRepository, Mockito.times(1)).save(section);
    }

    @Test
    void testUpdateSection() {
        Long sectionId = 1L;
        SectionDTO sectionDTO = new SectionDTO();
        sectionDTO.setName("UpdatedSection");
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        sectionDTO.setGeologicalClassList(List.of(geologicalClassDTO));

        Section existingSection = new Section();
        existingSection.setId(sectionId);
        GeologicalClass geologicalClass = new GeologicalClass();
        existingSection.setGeologicalClasses(List.of(geologicalClass));

        when(sectionRepository.getSectionsById(sectionId)).thenReturn(existingSection);

        when(sectionRepository.save(any(Section.class))).thenReturn(existingSection);

        CompletableFuture<SectionDTO> result = sectionService.updateSection(sectionId, sectionDTO);
        assertNotNull(result);
        assertEquals(sectionDTO.getName(), result.join().getName());

        verify(sectionRepository, Mockito.times(1)).save(existingSection);
    }

    @Test
    void testDeleteSection() {
        Section section = new Section();
        section.setId(1L);
        when(sectionRepository.getSectionsById(1L)).thenReturn(new Section());

        sectionService.deleteSection(1L).join();

        verify(sectionRepository, Mockito.times(1)).deleteById(1L);
    }
}
