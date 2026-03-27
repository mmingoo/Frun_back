package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserService userService;


    // 닉네임 중복 체크
    @Override
    public NicknameCheckResponse nicknameDuplicateCheck(String checkNickname) {
        // 닉네임 존재 여부
        boolean isExists = userRepository.existsByNickName(checkNickname);
        return new NicknameCheckResponse(isExists);
    }

    // 닉네임이 설정돼있는지 아닌지
    @Override
    public NicknameStatusResponse getNicknameStatus(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저의 닉네임 체크 여부
        boolean hasNickname = user.getNickName() != null && !user.getNickName().isBlank();
        return new NicknameStatusResponse(hasNickname);
    }

    @Override
    @Transactional
    public void setupProfile(Long userId, String nickname, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(nickname, imageUrl);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));    }

}
