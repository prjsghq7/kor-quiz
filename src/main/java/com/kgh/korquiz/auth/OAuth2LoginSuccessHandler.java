package com.kgh.korquiz.auth;

import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession session;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        if (oauthUser == null
                || oauthUser.getAttribute("provider") == null
                || oauthUser.getAttribute("sub") == null) {
            response.sendRedirect("/");
        }
        UserEntity user = this.userService.selectByProviderId(oauthUser.getAttribute("provider"),
                oauthUser.getAttribute("sub"));
        if (user != null) {
            session.setAttribute("signedUser", user);
            response.sendRedirect("/quiz/");
        } else {
            // 세션에 사용자 정보 저장
            session.setAttribute("oauthUser", oauthUser);
            // 추가 정보 입력 페이지로 이동
            response.sendRedirect("/user/register/extra");
        }
    }
}