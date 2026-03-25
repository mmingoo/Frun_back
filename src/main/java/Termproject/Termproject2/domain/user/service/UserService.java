package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;

public interface UserService {
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);
    NicknameStatusResponse getNicknameStatus(Long userId);
    void setupProfile(Long userId, String nickname, String imageUrl);
}
