package com.kgh.korquiz.config;

import com.kgh.korquiz.services.GoogleOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GoogleOAuth2UserService googleOAuth2UserService;

    public SecurityConfig(GoogleOAuth2UserService googleOAuth2UserService) {
        this.googleOAuth2UserService = googleOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(googleOAuth2UserService)
                        )
                );

        return http.build();
    }
}
