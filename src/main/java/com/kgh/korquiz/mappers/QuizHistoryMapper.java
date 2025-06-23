package com.kgh.korquiz.mappers;

import com.kgh.korquiz.dtos.QuizResultDto;
import com.kgh.korquiz.entities.QuizHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuizHistoryMapper {
    int insertQuizHistory(@Param(value = "quizHistory")QuizHistoryEntity quizHistory);
}
