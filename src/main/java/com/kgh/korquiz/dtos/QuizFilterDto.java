package com.kgh.korquiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizFilterDto {
    private String wordGrade;
    private String partOfSpeach;
}
