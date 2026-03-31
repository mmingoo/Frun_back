package Termproject.Termproject2.domain.running.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RunningLogUpdateRequest {

    @NotNull
    private LocalDate runDate;

    private LocalTime runTime;

    @NotNull
    @DecimalMin(value = "0.01", message = "거리는 0보다 커야 합니다.")
    @DecimalMax(value = "100.00", message = "거리는 최대 100km입니다.")
    private BigDecimal distance;

    @NotNull
    @Min(value = 0, message = "분은 0 이상이어야 합니다.")
    @Max(value = 600, message = "러닝 시간은 최대 600분입니다.")
    private Integer durationMin;

    @NotNull
    @Min(value = 0, message = "초는 0 이상이어야 합니다.")
    @Max(value = 59, message = "초는 0~59 사이여야 합니다.")
    private Integer durationSec;

    @Size(max = 500, message = "메모는 최대 500자까지 입력할 수 있습니다.")
    private String memo;

    // 유지할 기존 이미지 URL 목록 (포함되지 않은 기존 이미지는 삭제됨)
    private List<String> keepImageUrls;

    private boolean isPublic;

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
