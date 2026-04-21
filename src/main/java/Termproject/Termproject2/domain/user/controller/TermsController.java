package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.request.TermsUpdateRequest;
import Termproject.Termproject2.domain.user.dto.response.UserTermsAgreementResponseDto;
import Termproject.Termproject2.domain.user.service.UserTermsAgreementService;
import Termproject.Termproject2.domain.user.service.TermsService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;
    private final UserTermsAgreementService userTermsAgreementService;
    private final JwtTokenExtractor jwtTokenExtractor;


    /**
     * [POST] /api/terms/agree
     * 약관 동의 저장 - 회원가입 시 필수 약관 동의 여부 포함 저장
     */
    @PostMapping("/agree")
    @Operation(summary = "약관 동의 저장 (회원가입 시 호출)")
    public ResponseEntity<ApiResponse<?>> agreeTerms(
            @RequestBody TermsAgreementRequest request
    ) {
        Long userId = jwtTokenExtractor.getUserId();
        termsService.saveAgreements(userId, request);

        return ResponseEntity.ok(ApiResponse.ok("성공적으로 약관을 동의하였습니다."));
    }

    /**
     * [PATCH] /api/terms/agree
     * 약관 동의 변경 - 필수 약관 미동의 시 예외 처리
     */
    @PatchMapping("/agree")
    @Operation(summary = "약관 동의 변경")
    public ResponseEntity<ApiResponse<?>> updateTerms(
            @RequestBody TermsUpdateRequest request
    ) {
        Long userId = jwtTokenExtractor.getUserId();
        termsService.updateAgreements(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 약관을 동의하였습니다."));
    }

    /**
     * [GET] /api/terms
     * 약관 내용 조회 - 전체 약관 목록 반환
     */
    @GetMapping
    @Operation(summary = "약관 내용 조회")
    public ResponseEntity<ApiResponse<?>> getTerms(){
        return ResponseEntity.ok(ApiResponse.ok( termsService.getTerms(), "성공적으로 약관을 조회했습니다"));
    }

    /**
     * [GET] /api/terms/my
     * 내 약관 동의 내역 조회
     */
    @GetMapping("/my")
    @Operation(summary = "유저가 현재 동의한 약관 내역 조회")
    public ResponseEntity<List<UserTermsAgreementResponseDto>> getMyTermsAgreements() {
        Long userId = jwtTokenExtractor.getUserId();
        return ResponseEntity.ok(userTermsAgreementService.getMyTermsAgreements(userId));
    }
}