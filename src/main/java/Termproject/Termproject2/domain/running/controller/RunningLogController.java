package Termproject.Termproject2.domain.running.controller;

import Termproject.Termproject2.domain.running.dto.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.service.FeedService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/running-logs")
@RequiredArgsConstructor
public class RunningLogController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final FeedService feedService;


    @GetMapping("/feed")
    @Operation(summary = "친구 feed 목록 조회", description = "친구들의 feed를 최신순으로 조회")
    public ApiResponse<?>getFriendsFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Long userId = jwtTokenExtractor.getUserId();


        //친구들 피드 가져오기
        return ApiResponse.ok(feedService.getFriendFeeds(userId, page, size), "친구 피드 조회 성공");



    }



}

