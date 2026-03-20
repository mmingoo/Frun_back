package Termproject.Termproject2.domain.member.controller;

import Termproject.Termproject2.domain.member.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.member.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.member.service.MemberService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenExtractor jwtTokenExtractor;

    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]{5,20}$";


    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    @GetMapping("/check")
    public ApiResponse<?> checkNickname(
            @Parameter(description = "확인할 닉네임", required = true)
            @RequestParam String checkNickname){

        if (!checkNickname.matches(NICKNAME_PATTERN)) {
            return ApiResponse.fail("닉네임은 5~20자의 한글, 영문 대/소문자, 숫자만 사용 가능하며 공백은 허용되지 않습니다.");
        }

        NicknameCheckResponse result = memberService.nicknameDuplicateCheck(checkNickname);

        if(result.isExists()){
            return ApiResponse.ok(result,"이미 사용 중인 닉네임입니다.");
        }else{
            return ApiResponse.fail("사용 가능한 닉네임입니다.");
        }
    }

    @Operation(summary = "닉네임 보유 여부 확인", description = "현재 로그인한 유저의 닉네임 설정 여부를 확인합니다.")
    @GetMapping("/me/nickname-status")
    public ApiResponse<NicknameStatusResponse> getNicknameStatus() {
        Long userId = jwtTokenExtractor.getUserId();
        NicknameStatusResponse result = memberService.getNicknameStatus(userId);
        return ApiResponse.ok(result, result.isHasNickname() ? "닉네임이 설정되어 있습니다." : "닉네임이 설정되지 않았습니다.");
    }
}
