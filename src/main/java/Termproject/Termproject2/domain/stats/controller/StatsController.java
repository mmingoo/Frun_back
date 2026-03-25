package Termproject.Termproject2.domain.stats.controller;

import Termproject.Termproject2.domain.stats.service.StatsService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final StatsService statsService;

    @GetMapping("/me/summary")
    @Operation(summary = "내 기록 요약 조회", description = "이번 주(월~오늘)와 이번 달(1일~오늘)의 러닝 요약을 조회합니다.")
    public ApiResponse<?> getMySummary() {
        Long userId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(statsService.getMySummary(userId), "조회되었습니다.");
    }
}
