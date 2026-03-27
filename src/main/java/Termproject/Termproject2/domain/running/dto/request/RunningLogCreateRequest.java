package Termproject.Termproject2.domain.running.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RunningLogCreateRequest {
    private LocalDate runDate;
    private BigDecimal distance;
    private int durationMin;
    private int durationSec;
    private String memo;
    private boolean isPublic;
}
