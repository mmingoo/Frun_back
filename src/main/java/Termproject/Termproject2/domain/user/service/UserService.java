package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.*;
import Termproject.Termproject2.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);
    NicknameStatusResponse getNicknameStatus(Long userId);
    void setupProfile(Long userId, String nickname, String imageUrl);
    User findById(Long userId);
    UserPageResponseDto getUserPageInfo(Long viewerId, Long targetUserId);
    UserProfileInfoResponse getUserInfo(Long userId);

    void updateUserProfile(Long userId, UserProfileUpdateRequestDto request, MultipartFile profileImage);
}
