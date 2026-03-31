package Termproject.Termproject2.domain.running.controller;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.service.FeedService;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
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

    @GetMapping("/users/{userId}/feeds")
    @Operation(summary = "유저 페이지 피드 목록 조회", description = "유저의 러닝 피드를 최신순으로 조회 (무한스크롤). 본인이면 비공개 피드도 포함.")
    public ApiResponse<?> getUserPageFeeds(
            @PathVariable Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long viewerId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(feedService.getUserPageFeeds(viewerId, userId, cursorId, size), "유저 페이지 피드 조회 성공");
    }

    @GetMapping("/feed")
    @Operation(summary = "메인 feed 목록(친구) 조회", description = "친구들의 feed를 최신순으로 조회 (무한스크롤)")
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
            @Valid @ModelAttribute RunningLogCreateRequest request,
            @RequestPart(value = "images" , required = false) List<MultipartFile> images
            ){
        Long userId = jwtTokenExtractor.getUserId();

        return ApiResponse.ok(
                runningLogService.createRunningLog(userId, request, images),
                "러닝일지가 등록되었습니다."
        );

    }

    /**
     * 친구의 러닝 일지만 조회 가능
     * */
    @GetMapping("/{runningLogId}/{authorId}")
    @Operation(summary = "러닝로그 상세 조회")
    public ApiResponse<?> getRunningLogDetail(
            @PathVariable Long runningLogId,
            @PathVariable Long authorId
    ){
        Long userId = jwtTokenExtractor.getUserId();

        // 러닝 일지 조회
        FriendFeedResponseDto friendFeedResponseDto = runningLogService.getFeed(runningLogId, authorId, userId);

        return ApiResponse.ok(friendFeedResponseDto, "성공적으로 피드를 조회하였습니다.");
    }

    @PatchMapping(value = "/{runningLogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "러닝 일지 수정", description = "이미지와 러닝 일지 수정")
    public ApiResponse<?> updateRunningLog(
            @Valid @ModelAttribute RunningLogUpdateRequest request,
            @PathVariable Long runningLogId,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ){

        Long userId = jwtTokenExtractor.getUserId();
        runningLogService.updateRunningLog(runningLogId, userId, request, newImages);
        return ApiResponse.ok("러닝일지가 수정되었습니다.");
    }

    @DeleteMapping(value = "/{runningLogId}")
    @Operation(summary = "러닝 일지 soft 삭제", description = "러닝일지를 soft 삭제합니다")
    public ApiResponse<?> softDeleteRunningLog(
            @PathVariable Long runningLogId
    ){
        Long userId = jwtTokenExtractor.getUserId();
        runningLogService.softDeleteRunningLog(runningLogId, userId);
        return ApiResponse.ok( "러닝일지가 삭제되었습니다.");

    }

    @PostMapping("/likes/{runningLogId}")
    @Operation(summary = "러닝일지 좋아요")
    public ApiResponse<?> likeRunningLog(
            @PathVariable Long runningLogId
    ){
        // TODO: 구현 예정
        return ApiResponse.ok("좋아요가 처리되었습니다.");
    }




}
