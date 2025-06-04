package com.kgh.korquiz.services;

import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /** provider + providerId 로 유저 조회 */
    public UserEntity findByProviderId(String provider, String providerId) {
        return this.userMapper.selectUserByProviderId(provider, providerId);
    }

    /** 사용자 저장 */
    public void register(UserEntity user) {
        userMapper.insertUser(user);
    }

    /** 없으면 저장하고 리턴 (OAuth 로그인 시 사용) */
    public UserEntity findOrCreate(String provider, String providerId, String email, String profileImg) {
        UserEntity existing = findByProviderId(provider, providerId);
        if (existing != null) {
            return existing;
        }

        // 초기 생성 시 임시 정보 입력
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setProfileImg(profileImg);

        // 임시 정보 (사용자가 추가로 입력해야 하는 항목)
        int rand = (int)(Math.random() * 900000) + 100000; // 6자리
        newUser.setNickname("temp_" + rand);
        newUser.setBirth(LocalDate.of(2000, 1, 1));
        newUser.setGender("M");

        newUser.setTermAgreedAt(LocalDate.now());
        newUser.setAdmin(false);
        newUser.setDeleted(false);
        newUser.setSuspended(false);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setModifiedAt(LocalDateTime.now());

        register(newUser);
        return newUser;
    }

    /** ID로 유저 조회 (Controller 등에서 사용 가능) */
    public UserEntity findById(int id) {
        return userMapper.selectUserById(id);
    }
}
