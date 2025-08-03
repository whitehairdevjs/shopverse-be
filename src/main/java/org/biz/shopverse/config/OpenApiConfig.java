// src/main/java/com/example/js/config/OpenApiConfig.java
package org.biz.shopverse.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        // SecurityRequirement 에 정의한 이름("bearerAuth")과
        // Components에 추가한 스킴 이름이 반드시 일치해야 합니다.
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            // 1) API 기본 정보 설정
            .info(new Info()
                .title("ShopVerse 애플리케이션 API 문서")
                .version("v1.0.0")
                .description("js world Spring Boot Server")
                .contact(new Contact()
                    .name("Jae Sung Kim")
                    .email("jsuserwork20@gmail.com")
                )
            )
            // 2) 보안 스킴 정의 (JWT Bearer)
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            // 3) 전체 API에 보안 스킴 적용
            .addSecurityItem(new SecurityRequirement()
                .addList(securitySchemeName)
            );
    }
}