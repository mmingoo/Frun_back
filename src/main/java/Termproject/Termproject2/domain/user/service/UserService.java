package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    //TODO: 닉네임 중복 여부 확인
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);

    //TODO: 닉네임 설정 여부 확인
    NicknameStatusResponse getNicknameStatus(Long userId);

    //TODO: 최초 프로필 설정 (닉네임 + 이미지)
    void setupProfile(Long userId, String nickname, String imageUrl);

    //TODO: userId로 유저 조회
    User findById(Long userId);

    //TODO: 유저 페이지 정보 조회 (친구 수, 러닝 통계, 친구 상태 포함)
    UserPageResponseDto getUserPageInfo(Long viewerId, Long targetUserId);

    //TODO: nav바용 유저 정보 조회 (프로필 이미지, 닉네임, 알림 수)
    UserProfileInfoResponse getUserInfo(Long userId);

    //TODO: 유저 프로필 수정 (bio, 이미지)
    void updateUserProfile(Long userId, UserProfileUpdateRequestDto request, MultipartFile profileImage);

    //TODO: 닉네임 포함 유저 페이지 조회
    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    //TODO: 닉네임 포함 유저 커서 기반 조회
    List<User> findByNicknameContainingWithCursor(String keyword, String cursorName, Long cursorId, int size);

    //TODO: 계정 비활성화
    Long userDeactivate(Long userId);

    //TODO: 계정 활성화
    void userActivate(Long userId);

    //TODO: 비활성화 계정 정보 조회
    InactiveInfoResponse getInactiveInfo(Long userId);

    //TODO: 닉네임 변경
    void updateUserNickname(Long userId, @Valid UserUpdateNicknameDto request);

    //TODO: 회원 탈퇴
    void deleteUser(Long userId);
}
