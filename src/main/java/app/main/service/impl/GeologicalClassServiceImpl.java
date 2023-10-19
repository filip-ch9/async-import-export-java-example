package app.main.service.impl;

import app.main.converter.ConvertToDTO;
import app.main.dto.GeologicalClassDTO;
import app.main.entity.GeologicalClass;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.SectionRepository;
import app.main.service.GeologicalClassService;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static app.main.converter.ConvertToEntity.convertToGeologicalClassToVO;

@Service
@RequiredArgsConstructor
public class GeologicalClassServiceImpl implements GeologicalClassService {

    private final GeologicalClassRepository geologicalClassRepository;
    private final SectionRepository sectionRepository;
    @Override
    public CompletableFuture<GeologicalClassDTO> getGeologicalClassById(Long geologicalClassId) {
        return CompletableFuture.supplyAsync(() ->
            geologicalClassRepository.getGeologicalClassById(geologicalClassId)
        ).thenApply(ConvertToDTO::convertGeologicalClassToDto);
    }
    @Override
    public CompletableFuture<GeologicalClassDTO> createGeologicalClass(GeologicalClassDTO geologicalClassDTO) {
        return CompletableFuture.supplyAsync(() -> {
            GeologicalClass geologicalClass = convertToGeologicalClassToVO(geologicalClassDTO);
            if (Objects.nonNull(geologicalClassDTO.getSectionId())) {
                geologicalClass.setSection(sectionRepository.getSectionsById(geologicalClassDTO.getSectionId()));
            }
            return geologicalClassRepository.save(geologicalClass);
        }).thenApply(ConvertToDTO::convertGeologicalClassToDto);
    }

    @Override
    public CompletableFuture<GeologicalClassDTO> updateGeologicalClass(Long geologicalClassId, GeologicalClassDTO geologicalClassDTO) {
        return CompletableFuture.supplyAsync(() -> {
            GeologicalClass geologicalClass = geologicalClassRepository.getGeologicalClassById(geologicalClassId);
            if (geologicalClass == null) {
                return null;
            }
            geologicalClass.setCode(geologicalClassDTO.getCode());
            geologicalClass.setName(geologicalClassDTO.getName());

            if (Objects.nonNull(geologicalClassDTO.getSectionId())) {
                geologicalClass.setSection(sectionRepository.getSectionsById(geologicalClassDTO.getSectionId()));
            }
            return geologicalClassRepository.save(geologicalClass);
        }).thenApply(ConvertToDTO::convertGeologicalClassToDto);
    }

    @Override
    public CompletableFuture<Boolean> deleteGeologicalClassById(Long geologicalClassId) {
        return CompletableFuture.supplyAsync(() -> {
            geologicalClassRepository.deleteById(geologicalClassId);
            return geologicalClassRepository.getGeologicalClassById(geologicalClassId) == null;
        });
    }


}
