package com.kgh.korquiz.mappers;

import com.kgh.korquiz.entities.MeaningEntity;
import com.kgh.korquiz.entities.QuizEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MeaningMapper {
    public MeaningEntity[] selectByTargetCodeAndLanguageCode(@Param("targetCode") String targetCode,
                                                             @Param("languageCode") int languageCode);

    public int insertMeaning(@Param(value = "meanings") MeaningEntity meaning);
}
