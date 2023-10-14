package app.main.converter;

import app.main.dto.AsyncJobDTO;
import app.main.dto.GeologicalClassDTO;
import app.main.dto.ParsingResultDTO;
import app.main.dto.SectionDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Job;
import app.main.entity.Section;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ConvertToDTO {

    public static SectionDTO convertSectionToDto(Section section) {
        return new SectionDTO(section.getName(), convertGeologicalClassesListToDto(section.getGeologicalClasses()));
    }

    public static List<SectionDTO> convertListToSectionDto(List<Section> sectionList) {
        return sectionList.stream()
                .map(ConvertToDTO::convertSectionToDto)
                .toList();
    }

    public static SectionDTO convertSectionWithGeologicalClasListToDto(Section section, List<GeologicalClass> geologicalClasses) {
        return new SectionDTO(section.getName(), convertGeologicalClassesListToDto(geologicalClasses));
    }

    public static List<GeologicalClassDTO> convertGeologicalClassesListToDto(List<GeologicalClass> geologicalClassList) {
        return geologicalClassList
                .stream()
                .map(g -> new GeologicalClassDTO(g.getName(), g.getCode()))
                .toList();
    }

    public static GeologicalClassDTO convertGeologicalClassToDto(GeologicalClass geologicalClass) {
        Long id = Objects.nonNull(geologicalClass.getSection().getId()) ? geologicalClass.getSection().getId() : null;
        return new GeologicalClassDTO(geologicalClass.getName(), geologicalClass.getCode(), id);
    }

    public static AsyncJobDTO convertToAsyncJobDTO(Job job, LocalDateTime endTime) {
        return new AsyncJobDTO(job.getStatus().name(), job.getJobType(), job.getId(), job.getStartTime(), endTime);
    }

}
