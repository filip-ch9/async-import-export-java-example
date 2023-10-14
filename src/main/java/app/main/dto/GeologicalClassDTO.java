package app.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeologicalClassDTO {
    private String name;
    private String code;
    private Long sectionId;

    public GeologicalClassDTO(String name, String code) {
        this.code = code;
        this.name = name;
    }
}
