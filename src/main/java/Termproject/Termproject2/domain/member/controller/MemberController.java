package Termproject.Termproject2.domain.member.controller;

import Termproject.Termproject2.domain.member.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.member.service.MemberService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 닉네임 중복 확인
     * GET /api/v1/members/nickname/check?nickname={nickname}
     * 닉네임 가능 → 200 OK
     * 닉네임 중복 → 409 Conflict
    * */
    @GetMapping("/check")
    public ApiResponse<?> checkNickname(@RequestParam String checkNickname){
        NicknameCheckResponse result = memberService.nicknameDuplicateCheck(checkNickname);

        if(result.isExists()){
            return ApiResponse.ok(result,"사용 가능한 닉네임입니다.");
        }else{
            return ApiResponse.fail("이미 사용 중인 닉네임입니다.");
        }
    }
}
