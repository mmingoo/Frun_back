package Termproject.Termproject2.global.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 회원
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // 러닝로그
    RUNNING_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 러닝 로그입니다."),

    // 친구
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 친구 요청입니다."),
    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구 관계입니다."),

    // 이미지
    IMAGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일 크기는 3MB를 초과할 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (jpg, jpeg, png만 허용)"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "올바른 파일명이 아닙니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다."),
    INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "거리를 0km 이상으로 설정해주세요" ),
    INVALID_DURATION(HttpStatus.BAD_REQUEST, "시간을 0초 이상으로 설정해주세요." );

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
