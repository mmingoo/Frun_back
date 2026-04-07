package Termproject.Termproject2.domain.user.controller;

import Termproject.Termproject2.domain.user.dto.response.NicknameCheckResponse;
import Termproject.Termproject2.domain.user.dto.response.NicknameStatusResponse;
import Termproject.Termproject2.domain.user.dto.response.UserProfileUpdateRequestDto;
import Termproject.Termproject2.domain.user.dto.response.UserUpdateNicknameDto;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ApiResponse;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import Termproject.Termproject2.global.jwt.JWTUtil;
import Termproject.Termproject2.global.jwt.JwtTokenExtractor;
import Termproject.Termproject2.global.jwt.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated  // @RequestParam에 @Pattern 등 Bean Validation 적용을 위해 필요
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]{5,20}$";
    private static final String NICKNAME_MESSAGE = "닉네임은 5~20자의 한글, 영문 대/소문자, 숫자만 사용 가능하며 공백은 허용되지 않습니다.";


    @GetMapping("/check")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    public ApiResponse<NicknameCheckResponse> checkNickname(
            @Parameter(description = "확인할 닉네임", required = true)
            @RequestParam
            @NotBlank
            @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
            String checkNickname) {

        // 포맷 검증은 @Pattern이 처리 → 여기선 순수하게 중복 여부만 반환
        NicknameCheckResponse result = userService.nicknameDuplicateCheck(checkNickname);

        if (result.isExists()) {
            return ApiResponse.ok(result, "이미 사용 중인 닉네임입니다.");
        } else {
            return ApiResponse.ok(result, "사용 가능한 닉네임입니다.");
        }
    }

    @GetMapping("/me/nickname-status")
    @Operation(summary = "닉네임 보유 여부 확인", description = "현재 로그인한 유저의 닉네임 설정 여부를 확인")
    public ApiResponse<NicknameStatusResponse> getNicknameStatus() {
        Long userId = jwtTokenExtractor.getUserId();
        NicknameStatusResponse result = userService.getNicknameStatus(userId);
        return ApiResponse.ok(result, result.isHasNickname() ? "닉네임이 설정되어 있습니다." : "닉네임이 설정되지 않았습니다.");
    }

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

        Long userId = jwtTokenExtractor.getUserId();

        // 이미지 업로드
        String imageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imageUrl = imageService.saveProfileImage(userId, profileImage);
        }

        // 중복 체크 포함한 프로필 설정은 서비스에서 처리
        userService.setupProfile(userId, nickname, imageUrl);
        return ApiResponse.ok("프로필 설정이 완료되었습니다.");
    }

    @GetMapping("/{userId}/mypage")
    @Operation(summary = "유저 페이지 정보 조회", description = "유저의 페이지 정보를 조회합니다. 본인이면 isOwner=true, isFriend=false.")
    public ApiResponse<?> getUserPage(@PathVariable Long userId) {
        Long viewerId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(userService.getUserPageInfo(viewerId, userId), "조회되었습니다.");
    }

    @GetMapping("/me")
    @Operation(summary = "nav바 유저 정보 조회(프로필 사진, userId, 유저 닉네임, 알림 갯수)", description = "유저의 정보를 조회합니다. 프로필 이미지와 userId, 알림 갯수 반환")
    public ApiResponse<?> getUserProfileInfo() {
        Long userId = jwtTokenExtractor.getUserId();
        return ApiResponse.ok(userService.getUserInfo(userId), "조회되었습니다.");
    }

    @PatchMapping("/profile")
    @Operation(summary = "유저 프로필 업데이트 (프로필 사진, bio)")
    public ApiResponse<?> updateUserProfile(
            @Valid @RequestParam(value = "bio") UserProfileUpdateRequestDto request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        Long userId = jwtTokenExtractor.getUserId();
        userService.updateUserProfile(userId, request, profileImage);
        return ApiResponse.ok("성공적으로 유저의 프로필을 업데이트 했습니다.");
    }

    @GetMapping("/inactive-info")
    @Operation(summary = "비활성화 계정 정보 조회", description = "임시 토큰으로 비활성화 날짜와 삭제 예정일을 반환합니다.")
    public ApiResponse<?> getInactiveInfo(@RequestHeader("Authorization") String bearerToken) {
        // JWT 토큰에서 userId 추출
        Long userId = extractUserIdFromTempToken(bearerToken);
        return ApiResponse.ok(userService.getInactiveInfo(userId), "비활성화 계정 정보를 조회했습니다.");
    }

    @PatchMapping("/activate")
    @Operation(summary = "유저 활성화", description = "임시 토큰으로 비활성화된 계정을 활성화하고 refreshToken 쿠키를 발급합니다.")
    public ResponseEntity<ApiResponse<?>> userActivate(
            @RequestHeader("Authorization") String bearerToken, // 토큰 요청
            HttpServletResponse response // 토크 발급 후 반환할 response
    ) {

        // JWT 토큰에서 userId 추출
        Long userId = extractUserIdFromTempToken(bearerToken);

        // User 활성화
        userService.userActivate(userId);

        // 유저 조회
        User user = userService.findById(userId);

        // refreshToken 생성
        String refreshToken = jwtUtil.createJwt("refresh", userId, user.getUserName(), user.getRole().toString(), 60 * 60 * 24 * 14 * 1000L);

        // refreshToken 저장
        refreshTokenService.save(userId, refreshToken);

        // refreshToken 담은 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        // response 헤더에 쿠키 담고 반환
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.ok(userId, "성공적으로 계정을 활성화하였습니다."));
    }


    @DeleteMapping("/deactivate")
    @Operation(summary = "유저 비활성화")
    public ResponseEntity<ApiResponse<?>> userDeactivate(){
        Long userId = jwtTokenExtractor.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(userService.userDeactivate(userId),"성공적으로 계정을 비활성화하였습니다."));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "유저 닉네임 변경 ")
    public ApiResponse<?> updateUserNickname(
            @Valid @RequestBody UserUpdateNicknameDto request) {
        Long userId = jwtTokenExtractor.getUserId();
        System.out.println("닉네임 변경 시작 : " + request.getNickname());
        userService.updateUserNickname(userId, request);
        return ApiResponse.ok("성공적으로 유저의 닉네임을 변경하였습니다.");
    }



    //TODO: 임시토큰에서 userId 추출
    private Long extractUserIdFromTempToken(String bearerToken) {
        // 토큰이 null 이거나 "Bearer " 로 지삭하지 않으면 권한 에러 발생
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 토큰 추출
        String token = bearerToken.substring(7);

        // 토큰 만료 여부 및 토큰의 카테고리 일치 여부 확인
        if (jwtUtil.isExpired(token) || !"temp".equals(jwtUtil.getCategory(token))) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return jwtUtil.getUserId(token);
    }
}