package Termproject.Termproject2.domain.running.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// 러닝일지 생성 요청 DTO
@Getter
@Setter
public class RunningLogCreateRequest {

    @NotNull
    private LocalDate runDate; // 러닝 날짜

    @NotNull
    @DecimalMin(value = "0.01", message = "거리는 0보다 커야 합니다.")
    @DecimalMax(value = "100.00", message = "거리는 최대 100km입니다.")
    private BigDecimal distance; // 러닝 거리 (km)

    @NotNull
    @Min(value = 0, message = "분은 0 이상이어야 합니다.")
    @Max(value = 600, message = "러닝 시간은 최대 600분입니다.")
    private Integer durationMin; // 소요 시간 (분)

    @NotNull
    @Min(value = 0, message = "초는 0 이상이어야 합니다.")
    @Max(value = 59, message = "초는 0~59 사이여야 합니다.")
    private Integer durationSec; // 소요 시간 (초)

    @Size(max = 500, message = "메모는 최대 500자까지 입력할 수 있습니다.")
    private String memo; // 메모

    private boolean isPublic; // 공개 여부

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
