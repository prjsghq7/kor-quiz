package com.kgh.korquiz.auth;

import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService; // 사용자 정보 조회용

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = "google";
        String providerId = oauthUser.getAttribute("sub");

        log.info("[SUCCESS] OAuth 로그인 성공: providerId = {}", providerId);

        // 사용자 DB 조회
        UserEntity user = userService.findByProviderId(provider, providerId);

        // 닉네임, 성별, 생년월일이 아직 없는 경우 - 추가 정보 입력 필요
        if (user.getNickname() == null || user.getGender() == null || user.getBirth() == null) {
            log.info("추가 정보 입력 필요. 사용자: {}", user.getEmail());
            response.sendRedirect("/user/register/extra");
        }
        // 일반 사용자 로그인 시 홈으로 이동
        response.sendRedirect("/");
    }
}