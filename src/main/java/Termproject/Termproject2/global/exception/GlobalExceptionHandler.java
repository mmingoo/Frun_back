package Termproject.Termproject2.global.exception;

import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // reissue 과정에서 발생하는 토큰 관련 에러 — 원인과 무관하게 클라이언트에는 통일된 메시지 반환
    // 서버 로그에는 실제 원인이 기록되므로 디버깅 가능
    private static final Set<ErrorCode> RELOGIN_REQUIRED = Set.of(
            ErrorCode.REFRESH_TOKEN_MISSING,
            ErrorCode.REFRESH_TOKEN_EXPIRED,
            ErrorCode.INVALID_TOKEN_CATEGORY,
            ErrorCode.INVALID_REFRESH_TOKEN
    );

    // 비즈니스 예외 — 5xx는 error, 나머지는 warn으로 로그 레벨 분리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        // 토큰 재발급 실패 시 보안상 구체적인 원인을 클라이언트에 노출하지 않음
        if (RELOGIN_REQUIRED.contains(errorCode)) {
            log.warn("[ReissueException] {}", errorCode.getMessage());
            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(ApiResponse.fail("로그인해주세요."));
        }

        if (errorCode.getStatus().is5xxServerError()) {
            log.error("[BusinessException] {}", errorCode.getMessage(), e);
        } else {
            log.warn("[BusinessException] {}", errorCode.getMessage());
        }
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }

    // @Valid 검증 실패 — 첫 번째 필드 에러 메시지만 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.INVALID_INPUT.getMessage());
        log.warn("[ValidationException] {}", message);
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.fail(message));
    }

    // 필수 쿼리 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException e) {
        log.warn("[MissingParam] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.fail(e.getParameterName() + " 파라미터는 필수입니다."));
    }

    // 예상치 못한 예외 — 클라이언트에 내부 오류 세부 정보 노출 방지
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Exception] ", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}