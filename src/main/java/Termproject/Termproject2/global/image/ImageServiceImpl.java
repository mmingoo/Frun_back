package Termproject.Termproject2.global.image;

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

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public String uploadProfileImage(Long userId, MultipartFile file) {
        validateImage(file);

        String ext = getExtension(file.getOriginalFilename());
        String fileName = userId + "_" + UUID.randomUUID() + "." + ext;

        try {
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);
            Files.write(dirPath.resolve(fileName), file.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
        }

        return urlPrefix + "/" + fileName;
    }

    private void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지 파일 크기는 3MB를 초과할 수 없습니다.");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new IllegalArgumentException("jpg, jpeg, png 형식의 이미지만 업로드할 수 있습니다.");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("올바른 파일명이 아닙니다.");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
