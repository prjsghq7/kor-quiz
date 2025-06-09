package com.kgh.korquiz.regexes;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserRegex {
    // 닉네임: 한글, 영문, 숫자 2~15자
    public static final Regex nickname = new Regex("^([\\da-zA-Z가-힣]{2,15})$");
    // 생년월일: YYYY-MM-DD 형식 (1900~2099년 범위)
    public static final Regex birth = new Regex("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    // 성별: "M" 또는 "F"
    public static final Regex gender = new Regex("^[MF]$");
}
