package com.kgh.korquiz.services;

import com.kgh.korquiz.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public GoogleOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = delegate.loadUser(userRequest); // 실제 사용자 정보 요청

        String providerId = oauthUser.getAttribute("sub");
        if (providerId == null) {
            throw new IllegalArgumentException("OAuth2 응답에 'sub'이 존재하지 않습니다.");
        }

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        // provider 값 추가 (회원 가입시 저장용, OAuth2User의 attribute에 해당값 없음 -> 추가 필요)
        String provider = userRequest.getClientRegistration().getRegistrationId(); // 예: "google"
        attributes.put("provider", provider);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_TEMP")), //임시회원(추가 입력필요)
                attributes,
                "sub"
        );
    }

}
