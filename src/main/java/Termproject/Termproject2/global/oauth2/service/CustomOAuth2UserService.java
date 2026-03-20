package Termproject.Termproject2.global.oauth2.service;

import Termproject.Termproject2.domain.member.entity.Member;
import Termproject.Termproject2.domain.member.entity.Role;
import Termproject.Termproject2.domain.member.entity.SocialLoginType;
import Termproject.Termproject2.domain.member.repository.MemberRepository;
import Termproject.Termproject2.domain.member.repository.SocialLoginTypeRepository;
import Termproject.Termproject2.global.oauth2.dto.CustomOAuth2User;
import Termproject.Termproject2.global.oauth2.dto.NaverResponse;
import Termproject.Termproject2.global.oauth2.dto.OAuth2Response;
import Termproject.Termproject2.global.oauth2.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SocialLoginTypeRepository socialLoginTypeRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 소셜 제공자로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 소셜 제공자인지 확인 (naver, google 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        // 3. 제공자별 응답 파싱 (현재 네이버만 지원)
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else {
            // 지원하지 않는 소셜 제공자는 null 반환
            return null;
        }

        // 4. 제공자명 + 제공자 고유 ID 조합으로 서비스 username 생성
        //    ex) "naver_abc123", "google_xyz789" → 제공자가 달라도 ID 충돌 방지
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // 5. username으로 기존 회원 조회
        Member existMember = memberRepository.findByUserName(username);

        if (existMember == null) {
            // 6-1. 신규 회원 → DB에 회원 정보 저장


            Member memberEntity = Member.builder()
                    .userName(username)
                    .name(oAuth2Response.getName())
                    .userEmail(oAuth2Response.getEmail())
                    .providerId(oAuth2Response.getProviderId())
                    .userPhone(oAuth2Response.getPhone())
                    .role(Role.USER)
                    .socialLoginType(getSocialType(registrationId))
                    .build();


            Member savedMember = memberRepository.save(memberEntity);

            // 6-2. Security 인증에 사용할 DTO 생성 (신규 회원 = 기본 USER 권한)
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(savedMember.getUserId());
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(Role.USER);
            userDTO.setNewUser(true);

            return new CustomOAuth2User(userDTO);

        } else {
            // 7-1. 기존 회원 → DB에 저장된 role 그대로 사용 (관리자 권한 유지)
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(existMember.getUserId());
            userDTO.setUsername(existMember.getUserName());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existMember.getRole());
            userDTO.setNewUser(false);

            return new CustomOAuth2User(userDTO);
        }
    }

    private SocialLoginType getSocialType(String registrationId){
        SocialLoginType socialLoginType = socialLoginTypeRepository.findByTypeName(registrationId.toUpperCase());
        return socialLoginType;
    }
}
