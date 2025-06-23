package com.kgh.korquiz.entities;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizEntity {
    private String code;
    private String answer;
    private String partOfSpeach;
    private String wordGrade;
    private String link;
}
