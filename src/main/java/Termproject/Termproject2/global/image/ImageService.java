package Termproject.Termproject2.global.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    // 프로필 이미지 저장 → 파일명만 반환
    String saveProfileImage(Long userId, MultipartFile file);

    // 파일명 → 프로필 이미지 전체 URL
    String getProfileImageUrl(String fileName);

    // 러닝 로그 이미지 저장 → 파일명만 반환
    String saveRunningLogImage(Long userId, MultipartFile file);

    // 파일명 → 러닝 로그 이미지 전체 URL
    String getRunningLogImageUrl(String fileName);
}
