package app.main.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsyncJobDTO {
    private String jobType;
    private String status;
    private Long asyncJobId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
