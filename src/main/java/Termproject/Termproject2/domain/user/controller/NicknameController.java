package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.dto.response.UserUpdateNicknameDto;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.image.ImageService;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Nickname", description = "닉네임 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class NicknameController {

    private final UserService userService;
    private final ImageService imageService;

    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]{5,20}$";
    private static final String NICKNAME_MESSAGE = "닉네임은 5~20자의 한글, 영문 대/소문자, 숫자만 사용 가능하며 공백은 허용되지 않습니다.";

    /**
     * [GET] /api/v1/users/check
     * 닉네임 중복 확인 - 포맷 검증(@Pattern)은 컨트롤러에서, 중복 여부는 서비스에서 처리
     */
    @GetMapping("/check")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    public ApiResponse<NicknameCheckResponse> checkNickname(
            @Parameter(description = "확인할 닉네임", required = true)
            @RequestParam
            @NotBlank
            @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
            String checkNickname) {

        NicknameCheckResponse result = userService.nicknameDuplicateCheck(checkNickname);

        if (result.isExists()) {
            return ApiResponse.ok(result, "이미 사용 중인 닉네임입니다.");
        } else {
            return ApiResponse.ok(result, "사용 가능한 닉네임입니다.");
        }
    }

    /**
     * [GET] /api/v1/users/me/nickname-status
     * 닉네임 설정 여부 확인 - 소셜 로그인 후 닉네임 미설정 유저 판별용
     */
    @GetMapping("/me/nickname-status")
    @Operation(summary = "닉네임 보유 여부 확인", description = "현재 로그인한 유저의 닉네임 설정 여부를 확인")
    public ApiResponse<NicknameStatusResponse> getNicknameStatus() {
        Long userId = JwtTokenExtractor.getUserId();
        NicknameStatusResponse result = userService.getNicknameStatus(userId);
        return ApiResponse.ok(result, result.isHasNickname() ? "닉네임이 설정되어 있습니다." : "닉네임이 설정되지 않았습니다.");
    }

    /**
     * [POST] /api/v1/users/nickname-setup
     * 최초 프로필 설정 - 닉네임 + 프로필 이미지 저장 (소셜 로그인 후 1회 호출)
     */
    @PostMapping(value = "/nickname-setup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 설정 (최초 닉네임 설정)", description = "닉네임과 프로필 사진을 함께 저장합니다. 이미지는 최대 3MB, jpg/jpeg/png만 허용")
    public ApiResponse<?> setupProfile(
            @Parameter(description = "닉네임 (5~20자, 한글/영문/숫자)", required = true)
            @RequestParam
            @NotBlank
            @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
            String nickname,
            @Parameter(description = "프로필 이미지 (최대 3MB, jpg/jpeg/png)")
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        Long userId = JwtTokenExtractor.getUserId();

        String imageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imageUrl = imageService.saveProfileImage(userId, profileImage);
        }

        userService.setupProfile(userId, nickname, imageUrl);

        return ApiResponse.ok("프로필 설정이 완료되었습니다.");
    }

    /**
     * [PATCH] /api/v1/users/nickname
     * 닉네임 변경
     */
    @PatchMapping("/nickname")
    @Operation(summary = "유저 닉네임 변경")
    public ApiResponse<?> updateUserNickname(
            @Valid @RequestBody UserUpdateNicknameDto request) {
        Long userId = JwtTokenExtractor.getUserId();
        userService.updateUserNickname(userId, request);
        return ApiResponse.ok("성공적으로 유저의 닉네임을 변경하였습니다.");
    }
}
