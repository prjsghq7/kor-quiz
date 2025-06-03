package com.kgh.korquiz.mappers;

import com.kgh.korquiz.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public int insertUser(@Param("user") UserEntity user);
}
