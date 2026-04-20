package Termproject.Termproject2.domain.comment.controller;

import Termproject.Termproject2.domain.comment.dto.request.CommentCreateRequest;
import Termproject.Termproject2.domain.comment.service.CommentService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회" , description = "댓글 목록 조회, 무한 스크롤 방식, 30개씩 조회")
    @GetMapping("/running-logs/{running_log_id}/comments")
    public ResponseEntity<ApiResponse<?>> loadComment(
            @PathVariable Long running_log_id,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "30") int size
    ){
        return ResponseEntity.ok(ApiResponse.ok(commentService.getComment(running_log_id, cursorId, size), "댓글을 성공적으로 조회하였습니다."));
    }


    @Operation(summary = "답글 목록 조회" , description = "답글 목록 조회, 무한 스크롤 방식, 15개씩 조회")
    @GetMapping("/running-logs/reply/{parentId}")
    public ResponseEntity<ApiResponse<?>> loadReply(
            @PathVariable Long parentId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "30") int size
    ){
        return ResponseEntity.ok(ApiResponse.ok(commentService.getReplies(parentId, cursorId, size), "답글을 성공적으로 조회하였습니다."));
    }

    @Operation(summary = "댓글 생성")
    @PostMapping("/running-logs/{running_log_id}/comments")
    public ResponseEntity<ApiResponse<?>> createComment(
            @PathVariable Long running_log_id,
            @RequestBody @Valid CommentCreateRequest request
    ){

        Long userId = jwtTokenExtractor.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(commentService.createComment(running_log_id, userId, request), "댓글을 성공적으로 생성하였습니다."));
    }

    @Operation(summary = "답글 생성")
    @PostMapping("/running-logs/{running_log_id}/{parentId}/comments")
    public ResponseEntity<ApiResponse<?>> createReply(
            @PathVariable Long running_log_id,
            @PathVariable(required = false) Long parentId,
            @RequestBody @Valid CommentCreateRequest request
    ){
        Long userId = jwtTokenExtractor.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(commentService.createReply(running_log_id, userId,parentId, request), "답글을 성공적으로 생성하였습니다."));
    }

    @Operation(summary = "댓글/답글 수정")
    @PatchMapping("/running-logs/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentCreateRequest request
    ){

        Long userId = jwtTokenExtractor.getUserId();
        commentService.updateComment(commentId, userId, request);
        return ResponseEntity.ok( ApiResponse.ok("댓글을 성공적으로 수정하였습니다."));
    }

    @Operation(summary = "댓글/답글 삭제")
    @DeleteMapping("/running-logs/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable Long commentId
    ){

        Long userId = jwtTokenExtractor.getUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok( ApiResponse.ok("댓글을 성공적으로 삭제하였습니다."));
    }

}
