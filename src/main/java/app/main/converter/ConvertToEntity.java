package app.main.converter;

import app.main.dto.GeologicalClassDTO;
import app.main.dto.SectionDTO;
import app.main.entity.GeologicalClass;
import app.main.entity.Section;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConvertToEntity {

    public static Section convertSectionToVO(SectionDTO sectionDTO) {
        return new Section(sectionDTO.getName());
    }

    public static List<GeologicalClass> convertGeologicalClassesDtoList(List<GeologicalClassDTO> geologicalClassList) {
        return geologicalClassList
                .stream()
                .map(g -> new GeologicalClass(g.getName(), g.getCode()))
                .toList();
    }

    public static GeologicalClass convertToGeologicalClassToVO(GeologicalClassDTO geologicalClassDTO) {
        return new GeologicalClass(geologicalClassDTO.getName(), geologicalClassDTO.getCode());
    }
}
