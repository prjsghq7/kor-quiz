package com.kgh.korquiz.regexes;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QuizRegex {
    // 한글 30자
//    public static final Regex answer = new Regex("^([가-힣]{1,30})$");
    //공백 포함
    public static final Regex answer =
            new Regex("^(?=.{1,30}$)[가-힣]+(?:\\s[가-힣]+)*$");
}
