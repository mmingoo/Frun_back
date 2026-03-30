package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.MyPageResponseDto;
import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.dto.response.UserPageResponseDto;
import Termproject.Termproject2.domain.user.entity.User;

public interface UserService {
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);
    NicknameStatusResponse getNicknameStatus(Long userId);
    void setupProfile(Long userId, String nickname, String imageUrl);
    User findById(Long userId);
    MyPageResponseDto getMyPageInfo(Long userId);
    UserPageResponseDto getUserPageInfo(Long viewerId, Long targetUserId);
}
