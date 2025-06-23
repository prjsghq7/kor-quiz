package com.kgh.korquiz.mappers;

import com.kgh.korquiz.dtos.QuizFilterDto;
import com.kgh.korquiz.entities.QuizEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizMapper {
    public QuizEntity selectRandomByFilter(@Param("QuizFilterDto") QuizFilterDto quizFilterDto);

    public QuizEntity selectByCode(@Param("code") String code);

    public int selectCountByCode(@Param("code") String code);

    public int insertQuiz(@Param(value = "quiz")QuizEntity quiz);
}
