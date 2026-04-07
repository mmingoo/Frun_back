package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.dto.request.TermsAgreementRequest;
import Termproject.Termproject2.domain.user.dto.response.TermsResponse;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.TermsService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;
    private final JwtTokenExtractor jwtTokenExtractor;


    // 약관 동의 저장 (회원가입 시 호출)
    @PostMapping("/agree")
    public ResponseEntity<ApiResponse<?>> agreeTerms(
            @RequestBody TermsAgreementRequest request
    ) {
        Long userId = jwtTokenExtractor.getUserId();

        termsService.saveAgreements(userId, request);

        return ResponseEntity.ok(ApiResponse.ok("성공적으로 약관을 동의하였습니다."));
    }
}