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
    USER_NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "작성자가 아닙니다." ),
    USER_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "이미 비활성화된 계정입니다." ),
    USER_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "이미 활성화된 계정입니다." ),
    TERM_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "필수 약관을 선택해야 합니다."),


    // 러닝로그
    RUNNING_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 러닝 로그입니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시물입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 누르지 않은 게시물입니다."),
    INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "거리를 0km 이상으로 설정해주세요" ),
    INVALID_DURATION(HttpStatus.BAD_REQUEST, "시간을 0초 이상으로 설정해주세요." ),

    // 친구
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 친구 요청입니다."),
    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구 관계입니다."),
    USER_NOT_FRIEND_WITH_LOG_AUTHOR(HttpStatus.NOT_FOUND, "유저와 러닝일지 작성자는 친구가 아닙니다."),
    RUNNING_LOG_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "해당 러닝 일지의 작성자가 아닙니다."),
    PRIVATE_RUNNING_LOG(HttpStatus.FORBIDDEN, "비공개 러닝 일지입니다."),
    NOT_FRIEND(HttpStatus.NOT_FOUND,"친구가 아닙니다."),
    REQUEST_COMPLETED(HttpStatus.ACCEPTED,"이미 처리된 요청입니다."),

    // 이미지
    IMAGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일 크기는 3MB를 초과할 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (jpg, jpeg, png만 허용)"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "올바른 파일명이 아닙니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "이미지는 최대 5장까지 업로드할 수 있습니다."),


    //댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다." ),
    EXCEEDED_COMMENT_DEPTH(HttpStatus.BAD_REQUEST, "답글에 답글을 달 수 없습니다." ),
    INVALID_COMMENT_PARENT(HttpStatus.BAD_REQUEST, "해당 러닝일지에 해당 댓글이 소속돼있지 않습니다." ),
    NOT_COMMENT_OWNER(HttpStatus.UNAUTHORIZED,"본인의 댓글만 수정/삭제할 수 있습니다."),

    // 공지사항
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다."),

    // 신고
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 신고입니다."),
    REPORT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 신고 유형입니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "이미 신고한 러닝일지입니다."),
    REPORT_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인을 신고할 수 없습니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 신고입니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
