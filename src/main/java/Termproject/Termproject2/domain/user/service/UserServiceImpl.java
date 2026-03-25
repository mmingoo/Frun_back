package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {
        boolean isExists = userRepository.existsByNickName(checkNickname);
        return new NicknameCheckResponse(isExists);
    }

    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        boolean hasNickname = user.getNickName() != null && !user.getNickName().isBlank();
        return new NicknameStatusResponse(hasNickname);
    }

    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        user.updateProfile(nickname, imageUrl);
    }
}
