package Termproject.Termproject2.domain.running.controller;

import Termproject.Termproject2.domain.running.dto.request.FeedSortType;
import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.request.RunningLogUpdateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.service.FeedService;
import Termproject.Termproject2.domain.running.service.LikeService;
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

    private final FeedService feedService;
    private final RunningLogService runningLogService;
    private final LikeService likeService;

    /**
     * [GET] /api/v1/running-logs/users/{userId}/feeds
     * 유저 페이지 피드 목록 조회 - 본인이면 비공개 포함, 정렬 지원 무한 스크롤
     * sortType: CREATED_AT(기본) | RUN_DATE | RUN_TIME | DISTANCE | PACE
     * cursorValue: CREATED_AT 제외 시 이전 응답의 nextCursorValue 값 전달
     */
    @GetMapping("/users/{userId}/feeds")
    @Operation(summary = "유저 페이지 피드 목록 조회", description = "유저의 러닝 피드를 조회 (무한스크롤). 본인이면 비공개 피드도 포함. sortType: CREATED_AT(기본) | RUN_DATE | RUN_TIME | DISTANCE | PACE")
    public ApiResponse<?> getUserPageFeeds(
            @PathVariable Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) String cursorValue,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "CREATED_AT") FeedSortType sortType
    ) {
        System.out.println(" 정렬기준 : " + sortType);
        Long viewerId = JwtTokenExtractor.getUserId();

        return ApiResponse.ok(feedService.getUserPageFeeds(viewerId, userId, cursorId, cursorValue, size, sortType), "유저 페이지 피드 조회 성공");
    }

    /**
     * [GET] /api/v1/running-logs/feed
     * 친구 피드 목록 조회 - 최신순 커서 기반 무한 스크롤
     */
    @GetMapping("/feed")
    @Operation(summary = "메인 feed 목록(친구) 조회", description = "친구들의 feed를 최신순으로 조회 (무한스크롤)")
    public ApiResponse<?> getFriendsFeeds(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "15") int size
    ) {
        Long userId = JwtTokenExtractor.getUserId();
        return ApiResponse.ok(feedService.getFriendFeeds(userId, cursorId, size), "친구 피드 조회 성공");
    }

    /**
     * [POST] /api/v1/running-logs
     * 러닝 일지 등록 - 이미지 최대 5장 포함, 공개 설정 시 통계 자동 누적
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "러닝 일지 등록", description = "이미지와 러닝 일지 저장")
    public ApiResponse<?> createRunningLog(
            @Valid @ModelAttribute RunningLogCreateRequest request,
            @RequestPart(value = "images" , required = false) List<MultipartFile> images
            ){
        Long userId = JwtTokenExtractor.getUserId();

        return ApiResponse.ok(
                runningLogService.createRunningLog(userId, request, images),
                "러닝일지가 등록되었습니다."
        );

    }

    /**
     * [GET] /api/v1/running-logs/{runningLogId}/{authorId}
     * 러닝로그 상세 조회 - 본인 또는 친구의 공개 일지만 조회 가능
     */
    @GetMapping("/{runningLogId}")
    @Operation(summary = "러닝로그 상세 조회")
    public ApiResponse<?> getRunningLogDetail(
            @PathVariable Long runningLogId
    ){
        Long userId = JwtTokenExtractor.getUserId();

        // 러닝 일지 조회
        FriendFeedResponseDto friendFeedResponseDto = runningLogService.getFeed(runningLogId, userId);

        return ApiResponse.ok(friendFeedResponseDto, "성공적으로 피드를 조회하였습니다.");
    }

    /**
     * [PATCH] /api/v1/running-logs/{runningLogId}
     * 러닝 일지 수정 - 이미지 교체 포함, 공개 여부 변경 시 통계 자동 조정
     */
    @PatchMapping(value = "/{runningLogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "러닝 일지 수정", description = "이미지와 러닝 일지 수정")
    public ApiResponse<?> updateRunningLog(
            @Valid @ModelAttribute RunningLogUpdateRequest request,
            @PathVariable Long runningLogId,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ){

        Long userId = JwtTokenExtractor.getUserId();
        runningLogService.updateRunningLog(runningLogId, userId, request, newImages);
        return ApiResponse.ok("러닝일지가 수정되었습니다.");
    }

    /**
     * [DELETE] /api/v1/running-logs/{runningLogId}
     * 러닝 일지 soft 삭제 - isDeleted=true, 공개 일지면 통계 차감
     */
    @DeleteMapping(value = "/{runningLogId}")
    @Operation(summary = "러닝 일지 soft 삭제", description = "러닝일지를 soft 삭제합니다")
    public ApiResponse<?> softDeleteRunningLog(
            @PathVariable Long runningLogId
    ){
        Long userId = JwtTokenExtractor.getUserId();
        runningLogService.softDeleteRunningLog(runningLogId, userId);
        return ApiResponse.ok( "러닝일지가 삭제되었습니다.");

    }

    /**
     * [POST] /api/v1/running-logs/likes/{runningLogId}
     * 러닝일지 좋아요 - 본인 글에는 알림 미전송
     */
    @PostMapping("/likes/{runningLogId}")
    @Operation(summary = "러닝일지 좋아요")
    public ApiResponse<?> likeRunningLog(
            @PathVariable Long runningLogId
    ){
        Long userId = JwtTokenExtractor.getUserId();
        likeService.addLike(userId, runningLogId);
        return ApiResponse.ok("좋아요가 처리되었습니다.");
    }

    /**
     * [DELETE] /api/v1/running-logs/likes/{runningLogId}
     * 러닝일지 좋아요 취소
     */
    @DeleteMapping("/likes/{runningLogId}")
    @Operation(summary = "러닝일지 좋아요 취소")
    public ApiResponse<?> unlikeRunningLog(
            @PathVariable Long runningLogId
    ) {
        Long userId = JwtTokenExtractor.getUserId();
        likeService.removeLike(userId, runningLogId);
        return ApiResponse.ok("좋아요가 취소되었습니다.");
    }
}
