package org.example.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener API")
                        .description("A simple URL shortener service (in-memory store)")
                        .version("0.0.1")
                        .contact(new Contact().name("Dev Team").email("dev@example.com"))
                        .license(new License().name("MIT")));
    }
}
