package Termproject.Termproject2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.profile.dir}")
    private String profileDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로필 이미지: /images/profile/** → uploads/profile/
        registry.addResourceHandler(profileUrlPrefix + "/**")
                .addResourceLocations("file:" + Paths.get(profileDir).toAbsolutePath() + "/");

        // 러닝 로그 이미지: /images/running-log/** → uploads/running-log/
        registry.addResourceHandler(runningLogUrlPrefix + "/**")
                .addResourceLocations("file:" + Paths.get(runningLogDir).toAbsolutePath() + "/");
    }
}
