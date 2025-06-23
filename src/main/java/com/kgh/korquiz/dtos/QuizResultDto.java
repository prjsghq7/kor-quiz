package com.kgh.korquiz.dtos;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizResultDto {
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private String link;
}
