package Termproject.Termproject2.domain.member.service;

import Termproject.Termproject2.domain.member.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.member.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.member.entity.Member;
import Termproject.Termproject2.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {
        boolean isExists = memberRepository.existsByNickName(checkNickname);
        return new NicknameCheckResponse(isExists);
    }

    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        boolean hasNickname = member.getNickName() != null && !member.getNickName().isBlank();
        return new NicknameStatusResponse(hasNickname);
    }

    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.updateProfile(nickname, imageUrl);
    }
}
