package Termproject.Termproject2.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String bio;
    private long friendCount;
    private long totalRunCount;
    private double totalDistanceKm;
    private String avgPace;
}
