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

    private static final long MAX_FILE_SIZE = 3L * 1024 * 1024; // 3MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    @Value("${file.upload.profile.dir}")
    private String profileUploadDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogUploadDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;


    @Override
    public String uploadProfileImage(Long userId, MultipartFile file) {
        return uploadImage(userId, file, profileUploadDir);
    }

    @Override
    public String getImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return profileUrlPrefix + "/" + fileName;
    }

    @Override
    public String uploadRunningLogImage(Long userId, MultipartFile file) {
        return uploadImage(userId, file, runningLogUploadDir);
    }

    @Override
    public String getRunningLogImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return runningLogUrlPrefix + "/" + fileName;
    }

    private String uploadImage(Long userId, MultipartFile file, String dir) {
        validateImage(file);
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

    private void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.IMAGE_TOO_LARGE);
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
