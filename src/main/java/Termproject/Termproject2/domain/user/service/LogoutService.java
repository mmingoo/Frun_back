package Termproject.Termproject2.domain.user.service;

public interface LogoutService {

    /**
     * case1 - Access Token 유효한 상태에서 로그아웃 시도: userId 추출 → refreshToken 삭제 + accessToken 블랙리스트 등록
     *       - Access Token이 탈취된 경우까지 고려하여 Redis accessToken 블랙리스트 등록
     *
     * case2 - Access Token 만료된 상황에서 로그아웃 시도: refreshToken 쿠키로 userId 추출 → refreshToken 삭제만
     */
    void logout(String bearerToken, String refreshToken);
}
