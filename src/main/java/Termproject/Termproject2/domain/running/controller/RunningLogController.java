package Termproject.Termproject2.domain.running.controller;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.service.FeedService;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/running-logs")
@RequiredArgsConstructor
public class RunningLogController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final FeedService feedService;
    private final RunningLogService runningLogService;

    @GetMapping("/feed")
    @Operation(summary = "친구 feed 목록 조회", description = "친구들의 feed를 최신순으로 조회 (무한스크롤)")
    public ApiResponse<?> getFriendsFeeds(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(feedService.getFriendFeeds(userId, cursorId, size), "친구 피드 조회 성공");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "러닝 일지 등록", description = "이미지와 러닝 일지 저장")
    public ApiResponse<?> createRunningLog(
            @ModelAttribute RunningLogCreateRequest request,
            @RequestPart(value = "images" , required = false) List<MultipartFile> images
            ){
        Long userId = jwtTokenExtractor.getUserId();

        return ApiResponse.ok(
                runningLogService.createRunningLog(userId, request, images),
                "러닝일지가 등록되었습니다."
        );

    }

}
