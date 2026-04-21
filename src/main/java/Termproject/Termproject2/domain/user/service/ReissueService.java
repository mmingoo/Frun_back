package Termproject.Termproject2.domain.user.service;

import Termproject.Termproject2.global.jwt.TokenPairDto;

public interface ReissueService {

    //TODO: RTR 방식으로 accessToken 발급 및 refreshToken 갱신
    TokenPairDto reissue(String refreshToken);
}
