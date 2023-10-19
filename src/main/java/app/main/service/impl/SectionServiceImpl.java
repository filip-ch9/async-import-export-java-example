package app.main.service.impl;

import app.main.converter.ConvertToDTO;
import app.main.dto.SectionDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Section;
import app.main.repository.GeologicalClassRepository;
import app.main.repository.SectionRepository;
import app.main.service.SectionService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static app.main.converter.ConvertToEntity.convertSectionToVO;
import static app.main.converter.ConvertToDTO.convertSectionWithGeologicalClasListToDto;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassRepository geologicalClassRepository;

    @Override
    public CompletableFuture<SectionDTO> getSection(Long id) {
        return CompletableFuture.supplyAsync(() -> sectionRepository.getSectionsById(id))
                .thenApply(ConvertToDTO::convertSectionToDto);
    }

    public CompletableFuture<List<SectionDTO>> getAllSectionsByGeologicalCode(String code) {
        return CompletableFuture.supplyAsync(() -> sectionRepository.findAllByGeologicalClasses(code))
                .thenApply(ConvertToDTO::convertListToSectionDto);
    }
    @Override
    public CompletableFuture<SectionDTO> saveSection(SectionDTO sectionDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Section section = sectionRepository.save(convertSectionToVO(sectionDTO));
            if (!sectionDTO.getGeologicalClassList().isEmpty()) {
                sectionDTO.getGeologicalClassList().forEach(g -> {
                    GeologicalClass geologicalClass = new GeologicalClass(g.getName(), g.getCode());
                    geologicalClass.setSection(section);
                    geologicalClassRepository.save(geologicalClass);
                });
            }
            return section;
        })
        .thenApply(section -> {
            List<GeologicalClass> list = geologicalClassRepository.findGeologicalClassBySection(section);
            return convertSectionWithGeologicalClasListToDto(section, list);
        });
    }
    @Override
    public CompletableFuture<SectionDTO> updateSection(Long sectionId, SectionDTO sectionDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Section section = sectionRepository.getSectionsById(sectionId);
            if (section == null) {
                return null;
            }
            section.setName(sectionDTO.getName());
            return sectionRepository.save(section);
        }).thenApply(ConvertToDTO::convertSectionToDto);
    }
    @Override
    public CompletableFuture<Boolean> deleteSection(Long sectionId) {
        return CompletableFuture.supplyAsync(() -> {
            sectionRepository.deleteById(sectionId);
            return sectionRepository.getSectionsById(sectionId) == null;
        });
    }
}
