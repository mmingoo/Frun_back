package Termproject.Termproject2.global.image;

import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    @Value("${file.upload.profile.dir}")
    private String profileDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;

    @Value("${file.upload.notice.url-prefix}")
    private String noticeUrlPrefix;

    // ── 프로필 이미지 ──────────────────────────────────────

    @Override
    public String saveProfileImage(Long userId, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.IMAGE_TOO_LARGE);
        }
        return save(userId, file, profileDir);
    }

    @Override
    public String getProfileImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return profileUrlPrefix + "/" + fileName;
    }

    // ── 러닝 로그 이미지 ────────────────────────────────────

    @Override
    public String saveRunningLogImage(Long userId, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.IMAGE_TOO_LARGE);
        }
        return save(userId, file, runningLogDir);
    }

    @Override
    public String getRunningLogImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return runningLogUrlPrefix + "/" + fileName;
    }

    @Override
    public String getNoticeImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return noticeUrlPrefix + "/" + fileName;
    }

    // ── 공통 내부 메서드 ────────────────────────────────────

    // 이미지 저장 메서드
    private String save(Long userId, MultipartFile file, String dir) {
        // 파일 검증 (용량, 확장자)
        validate(file);

        // 파일명에서 확장자 추출 (ex jpeg, jpng 추출)
        String ext = getExtension(file.getOriginalFilename());

        // userId + UUID 조합으로 파일명 중복 방지 (ex userId + "_" + UUID + ext)
        String fileName = userId + "_" + UUID.randomUUID() + "." + ext;

        try {
            // 저장 경로(/uploads/profile or /uploads/profile)를 Path 객체로 변환
            Path dirPath = Paths.get(dir);

            // 디렉토리가 없으면 생성
            Files.createDirectories(dirPath);

            // 지정 경로에 파일 (바이트 데이터) 저장
            Files.write(dirPath.resolve(fileName), file.getBytes());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED, e);
        }
        // 저장된 파일명 반환 (경로 제외)
        return fileName;
    }

    // 파일 검증 (확장자)
    private void validate(MultipartFile file) {
        // 허용되지 않는 확장자면 예외
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    // 파일명에서 확장자 추출
    private String getExtension(String originalFilename) {
        // 파일명이 없거나 확장자가 없는 경우 예외
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }
        // 마지막 점(.) 이후 문자열 반환 (e.g. "jpg", "png")
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }
}
