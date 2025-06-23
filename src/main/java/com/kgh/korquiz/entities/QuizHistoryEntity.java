package com.kgh.korquiz.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizHistoryEntity {
    private int historyId;
    private int userId;
    private String quizCode;
    private String userAnswer;
    private boolean isCorrect;
    private LocalDateTime solvedAt;
}
