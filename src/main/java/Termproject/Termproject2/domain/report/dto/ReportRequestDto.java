package Termproject.Termproject2.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 신고 접수 요청 DTO (러닝일지 전용)
 * - 신고 대상 러닝일지 ID, 신고 사유를 포함
 */
@Getter
@NoArgsConstructor
public class ReportRequestDto {

    @NotBlank(message = "신고 사유는 필수입니다.")
    @Schema(description = "신고 사유", example = "욕설 및 비방 내용이 포함되어 있습니다.")
    private String reportReason;
}
