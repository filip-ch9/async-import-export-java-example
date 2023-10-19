package app.main.service;

import app.main.dto.GeologicalClassDTO;
import java.util.concurrent.CompletableFuture;

public interface GeologicalClassService {

    CompletableFuture<GeologicalClassDTO> getGeologicalClassById(Long geologicalClassId);

    CompletableFuture<GeologicalClassDTO> createGeologicalClass(GeologicalClassDTO geologicalClassDTO);

    CompletableFuture<GeologicalClassDTO> updateGeologicalClass(Long geologicalClassId, GeologicalClassDTO geologicalClassDTO);

    CompletableFuture<Boolean> deleteGeologicalClassById(Long geologicalClassId);
}
