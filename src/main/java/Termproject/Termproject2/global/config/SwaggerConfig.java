package Termproject.Termproject2.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtScheme = "bearerAuth";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);

        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtScheme)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("FRun API")
                        .description("FRun 러닝 앱 API 문서")
                        .version("v1.0.0"))
                .addServersItem(new Server().url("http://localhost:8081"))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes(jwtScheme, securityScheme));
    }
}
