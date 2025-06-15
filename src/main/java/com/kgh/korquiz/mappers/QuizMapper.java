package com.kgh.korquiz.mappers;

import com.kgh.korquiz.entities.QuizEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizMapper {
    public int selectCountByCode(@Param("code") String code);

    public int insertQuiz(@Param(value = "quiz")QuizEntity quiz);
}
