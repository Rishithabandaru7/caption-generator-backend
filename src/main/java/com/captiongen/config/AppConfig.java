package com.captiongen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    // Read the allowed origins from application.properties
    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;

    /**
     * RestTemplate is used to make HTTP calls to the Groq API.
     * Registered as a Spring bean so it can be injected anywhere.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * CORS config — allows your frontend (Vercel / localhost) to call this backend.
     * Update cors.allowed.origins in application.properties after deployment.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

}