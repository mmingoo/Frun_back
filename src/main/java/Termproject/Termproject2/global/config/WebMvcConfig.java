package Termproject.Termproject2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.profile.dir}")
    private String profileUploadDir;

    @Value("${file.upload.profile.url-prefix}")
    private String profileUrlPrefix;

    @Value("${file.upload.running-log.dir}")
    private String runningLogUploadDir;

    @Value("${file.upload.running-log.url-prefix}")
    private String runningLogUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String profilePath = Paths.get(profileUploadDir).toAbsolutePath().toString();
        registry.addResourceHandler(profileUrlPrefix + "/**")
                .addResourceLocations("file:" + profilePath + "/");

        String runningLogPath = Paths.get(runningLogUploadDir).toAbsolutePath().toString();
        registry.addResourceHandler(runningLogUrlPrefix + "/**")
                .addResourceLocations("file:" + runningLogPath + "/");
    }
}
