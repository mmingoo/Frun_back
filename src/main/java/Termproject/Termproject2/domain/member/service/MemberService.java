package Termproject.Termproject2.domain.member.service;

import Termproject.Termproject2.domain.member.dto.response.NicknameCheckResponse;

public interface MemberService {
    NicknameCheckResponse nicknameDuplicateCheck(String checkNickname);
}
