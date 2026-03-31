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

    private static final long MAX_FILE_SIZE = 3L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    @Value("${file.upload.profile.dir}")
    private String profileDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;

    // ── 프로필 이미지 ──────────────────────────────────────

    @Override
    public String saveProfileImage(Long userId, MultipartFile file) {
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
        return save(userId, file, runningLogDir);
    }

    @Override
    public String getRunningLogImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return runningLogUrlPrefix + "/" + fileName;
    }

    // ── 공통 내부 메서드 ────────────────────────────────────

    private String save(Long userId, MultipartFile file, String dir) {
        validate(file);
        String ext = getExtension(file.getOriginalFilename());
        String fileName = userId + "_" + UUID.randomUUID() + "." + ext;
        try {
            Path dirPath = Paths.get(dir);
            Files.createDirectories(dirPath);
            Files.write(dirPath.resolve(fileName), file.getBytes());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED, e);
        }
        return fileName;
    }

    private void validate(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.IMAGE_TOO_LARGE);
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String getExtension(String originalFilename) {
        System.out.println("파일명 : " + originalFilename);
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }
}
