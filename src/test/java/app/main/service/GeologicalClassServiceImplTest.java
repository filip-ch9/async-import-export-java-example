package app.main.service;

import app.main.dto.GeologicalClassDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Section;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.SectionRepository;
import app.main.service.impl.GeologicalClassServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static app.main.converter.ConvertToDTO.convertGeologicalClassToDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeologicalClassServiceImplTest {

    @Mock
    private GeologicalClassRepository geologicalClassRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private GeologicalClassServiceImpl geologicalClassService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGeologicalClassById() {
        Long geologicalClassId = 1L;
        GeologicalClass geologicalClass = new GeologicalClass();
        when(geologicalClassRepository.getGeologicalClassById(geologicalClassId)).thenReturn(geologicalClass);

        CompletableFuture<GeologicalClassDTO> result = geologicalClassService.getGeologicalClassById(geologicalClassId);
        assertNotNull(result);
        assertEquals( convertGeologicalClassToDto(geologicalClass), result.join());

        verify(geologicalClassRepository, Mockito.times(1)).getGeologicalClassById(1L);
    }

    @Test
    void testCreateGeologicalClass() {
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        geologicalClassDTO.setCode("TestCode");
        geologicalClassDTO.setName("TestName");
        geologicalClassDTO.setSectionId(1L);

        Section section = new Section();
        section.setId(1L);
        when(sectionRepository.getSectionsById(geologicalClassDTO.getSectionId())).thenReturn(section);

        GeologicalClass geologicalClass = new GeologicalClass();
        geologicalClass.setCode("TestCode");
        geologicalClass.setName("TestName");
        geologicalClass.setSection(section);
        when(geologicalClassRepository.save(any(GeologicalClass.class))).thenReturn(geologicalClass);

        CompletableFuture<GeologicalClassDTO> result = geologicalClassService.createGeologicalClass(geologicalClassDTO);

        assertNotNull(result);
        GeologicalClassDTO resultDTO = result.join();
        assertEquals(geologicalClassDTO.getCode(), resultDTO.getCode());
        assertEquals(geologicalClassDTO.getName(), resultDTO.getName());
        assertEquals(geologicalClassDTO.getSectionId(), resultDTO.getSectionId());

        verify(sectionRepository, Mockito.times(1)).getSectionsById(1L);
        verify(geologicalClassRepository, Mockito.times(1)).save(geologicalClass);
    }

    @Test
    void testUpdateGeologicalClass() {
        Long geologicalClassId = 1L;
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        geologicalClassDTO.setCode("UpdatedCode");
        geologicalClassDTO.setName("UpdatedName");
        geologicalClassDTO.setSectionId(1L);

        GeologicalClass existingGeologicalClass = new GeologicalClass();
        when(geologicalClassRepository.getGeologicalClassById(geologicalClassId)).thenReturn(existingGeologicalClass);

        Section section = new Section();
        when(sectionRepository.getSectionsById(geologicalClassDTO.getSectionId())).thenReturn(section);

        when(geologicalClassRepository.save(any(GeologicalClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<GeologicalClassDTO> result = geologicalClassService.updateGeologicalClass(geologicalClassId, geologicalClassDTO);
        assertNotNull(result);
        assertEquals(geologicalClassDTO.getCode(), result.join().getCode());
        assertEquals(geologicalClassDTO.getName(), result.join().getName());

        verify(sectionRepository, Mockito.times(1)).getSectionsById(1L);
        verify(geologicalClassRepository, Mockito.times(1)).save(existingGeologicalClass);
    }

    @Test
    void testDeleteGeologicalClassById() {
        GeologicalClass geologicalClass = new GeologicalClass();
        geologicalClass.setId(1L);
        when(geologicalClassRepository.getGeologicalClassById(1L)).thenReturn(new GeologicalClass());
        geologicalClassService.deleteGeologicalClassById(1L).join();
        verify(geologicalClassRepository, Mockito.times(1)).deleteById(1L);
    }
}
