package Termproject.Termproject2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 서버 내부 경로가 그대로 외부에 노출하는 것을 방지해
     * 매핑 방식으로 이미지를 가져오는 방식
     *
     * 프로필 이미지: /images/profile/** → /uploads/profile/
     * 러닝일지 이미지 : /images/running-log/** → /uploads/running-log/
     * */

    @Value("${file.upload.profile.dir}")
    private String profileDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;

    @Value("${file.upload.notice.dir}")
    private String noticeDir;

    @Value("${file.upload.notice.url-prefix}")
    private String noticeUrlPrefix;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로필 이미지: /images/profile/** → uploads/profile/
        registry.addResourceHandler(profileUrlPrefix + "/**")
                .addResourceLocations("file:" + Paths.get(profileDir).toAbsolutePath() + "/");

        // 러닝 로그 이미지: /images/running-log/** → uploads/running-log/
        registry.addResourceHandler(runningLogUrlPrefix + "/**")
                .addResourceLocations("file:" + Paths.get(runningLogDir).toAbsolutePath() + "/");

        // 공지 이미지: /images/notice/** → uploads/notice/
        registry.addResourceHandler(noticeUrlPrefix + "/**")
                .addResourceLocations("file:" + Paths.get(noticeDir).toAbsolutePath() + "/");
    }
}
