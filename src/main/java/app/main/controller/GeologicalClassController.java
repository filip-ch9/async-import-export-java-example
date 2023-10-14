package app.main.controller;

import app.main.api.GeologicalClassEndpoint;
import app.main.dto.GeologicalClassDTO;
import app.main.service.GeologicalClassService;
import java.util.concurrent.CompletionStage;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeologicalClassController implements GeologicalClassEndpoint {

    private final GeologicalClassService geologicalClassService;

    public GeologicalClassController(GeologicalClassService geologicalClassService) {
        this.geologicalClassService = geologicalClassService;
    }

    @Override
    public CompletionStage<GeologicalClassDTO> createGeologicalClass(GeologicalClassDTO geologicalClassDTO) {
        return geologicalClassService.createGeologicalClass(geologicalClassDTO);
    }

    @Override
    public CompletionStage<GeologicalClassDTO> fetchGeologicalClass(Long geologicalClassId) {
        return geologicalClassService.getGeologicalClassById(geologicalClassId);
    }

    @Override
    public CompletionStage<GeologicalClassDTO> updateGeologicalClass(Long geologicalClassId, GeologicalClassDTO geologicalClassDTO) {
        return geologicalClassService.updateGeologicalClass(geologicalClassId, geologicalClassDTO);
    }

    @Override
    public CompletionStage<Boolean> deleteGeologicalClass(Long geologicalClassId) {
        return geologicalClassService.deleteGeologicalClassById(geologicalClassId);
    }
}
