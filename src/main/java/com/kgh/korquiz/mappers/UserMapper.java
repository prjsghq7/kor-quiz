package com.kgh.korquiz.mappers;

import com.kgh.korquiz.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public int insertUser(@Param("user") UserEntity user);

    public UserEntity selectUserByProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    public UserEntity selectUserById(@Param("id") int id);

    int selectCountByNickname(@Param(value = "nickname") String nickname);
}
