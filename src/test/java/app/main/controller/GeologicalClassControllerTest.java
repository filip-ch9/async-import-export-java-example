package app.main.controller;

import app.main.dto.GeologicalClassDTO;
import app.main.service.GeologicalClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GeologicalClassControllerTest {

    @Mock
    private GeologicalClassService geologicalClassService;

    @InjectMocks
    private GeologicalClassController geologicalClassController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGeologicalClass() {
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        geologicalClassDTO.setCode("TestCode");
        geologicalClassDTO.setName("TestName");

        when(geologicalClassService.createGeologicalClass(geologicalClassDTO))
                .thenReturn(CompletableFuture.completedFuture(geologicalClassDTO));

        CompletableFuture<GeologicalClassDTO> result = geologicalClassController.createGeologicalClass(geologicalClassDTO)
                .toCompletableFuture();
        assertEquals(geologicalClassDTO, result.join());
    }

    @Test
    void testFetchGeologicalClass() {
        Long geologicalClassId = 1L;
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        geologicalClassDTO.setSectionId(geologicalClassId);
        geologicalClassDTO.setCode("TestCode");
        geologicalClassDTO.setName("TestName");

        when(geologicalClassService.getGeologicalClassById(geologicalClassId))
                .thenReturn(CompletableFuture.completedFuture(geologicalClassDTO));

        CompletableFuture<GeologicalClassDTO> result = geologicalClassController.fetchGeologicalClass(geologicalClassId)
                .toCompletableFuture();
        assertEquals(geologicalClassDTO, result.join());
    }

    @Test
    void testUpdateGeologicalClass() {
        Long geologicalClassId = 1L;
        GeologicalClassDTO geologicalClassDTO = new GeologicalClassDTO();
        geologicalClassDTO.setCode("UpdatedCode");
        geologicalClassDTO.setName("UpdatedName");

        when(geologicalClassService.updateGeologicalClass(geologicalClassId, geologicalClassDTO))
                .thenReturn(CompletableFuture.completedFuture(geologicalClassDTO));

        CompletableFuture<GeologicalClassDTO> result = geologicalClassController.updateGeologicalClass(geologicalClassId, geologicalClassDTO)
                .toCompletableFuture();
        assertEquals(geologicalClassDTO, result.join());
    }

    @Test
    void testDeleteGeologicalClass() {
        Long geologicalClassId = 1L;

        when(geologicalClassService.deleteGeologicalClassById(geologicalClassId))
                .thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> result = geologicalClassController.deleteGeologicalClass(geologicalClassId)
                .toCompletableFuture();
        assertTrue(result.join());
    }
}
