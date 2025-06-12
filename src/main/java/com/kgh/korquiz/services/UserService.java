package com.kgh.korquiz.services;

import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.mappers.UserMapper;
import com.kgh.korquiz.regexes.UserRegex;
import com.kgh.korquiz.results.user.RegisterResult;
import com.kgh.korquiz.results.CommonResult;
import com.kgh.korquiz.results.Result;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * provider + providerId 로 유저 조회
     */
    public UserEntity selectByProviderId(String provider, String providerId) {
        return this.userMapper.selectUserByProviderId(provider, providerId);
    }

    /**
     * 사용자 저장
     */
    public Result register(UserEntity user, OAuth2User oauthUser) {
        if (oauthUser == null
                || oauthUser.getAttribute("email") == null
                || oauthUser.getAttribute("name") == null
                || oauthUser.getAttribute("provider") == null
                || oauthUser.getAttribute("sub") == null
                || oauthUser.getAttribute("picture") == null) {
            return RegisterResult.FAILURE_OAUTH_SESSION_EXPIRED;
        }
        if (user == null) {
            return CommonResult.FAILURE;
        }
        Result _isNicknameAvailable = this.isNicknameAvailable(user.getNickname());
        if (_isNicknameAvailable != CommonResult.SUCCESS) {
            return _isNicknameAvailable;
        }
        user.setEmail(oauthUser.getAttribute("email"));
        user.setName(oauthUser.getAttribute("name"));
        user.setProvider(oauthUser.getAttribute("provider"));
        user.setProviderId(oauthUser.getAttribute("sub"));
        user.setProfileImg(oauthUser.getAttribute("picture"));
        user.setTermAgreedAt(LocalDate.now());
        user.setAdmin(false);
        user.setDeleted(false);
        user.setSuspended(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        return this.userMapper.insertUser(user) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    /**
     * ID로 유저 조회 (Controller 등에서 사용 가능)
     */
    public UserEntity findById(int id) {
        return userMapper.selectUserById(id);
    }


    public Result isNicknameAvailable(String nickname) {
        if (!UserRegex.nickname.matches(nickname)) {
            return CommonResult.FAILURE;
        }
        return this.userMapper.selectCountByNickname(nickname) == 0
                ? CommonResult.SUCCESS
                : RegisterResult.FAILURE_DUPLICATE_NICKNAME;
    }
}
