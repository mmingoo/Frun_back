package Termproject.Termproject2.global.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadProfileImage(Long userId, MultipartFile file);
    String getImageUrl(String fileName);
    String uploadRunningLogImage(Long userId, MultipartFile file);
    String getRunningLogImageUrl(String fileName);
}
