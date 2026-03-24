package Termproject.Termproject2.domain.member.service;

import Termproject.Termproject2.domain.member.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.member.dto.response.NicknameStatusResponse;

public interface MemberService {
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);
    NicknameStatusResponse getNicknameStatus(Long userId);
    void setupProfile(Long userId, String nickname, String imageUrl);
}
