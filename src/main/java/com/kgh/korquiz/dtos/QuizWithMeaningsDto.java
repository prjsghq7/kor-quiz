package com.kgh.korquiz.dtos;

import com.kgh.korquiz.entities.MeaningEntity;
import com.kgh.korquiz.entities.QuizEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizWithMeaningsDto {
    private QuizEntity quiz;
    private MeaningEntity[] meanings;
}
