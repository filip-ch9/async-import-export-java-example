package app.main.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsingResultDTO {
    private List<SectionDTO> sectionList;
    private AsyncJobDTO asyncJobDTO;
}
