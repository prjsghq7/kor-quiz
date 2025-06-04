package com.kgh.korquiz.entities;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserEntity {
    private int id;
    private String email;
    private String provider;
    private String providerId;
    private String profileImg;
    private String nickname;
    private LocalDate birth;
    private String gender;
    private LocalDate termAgreedAt;
    private boolean isAdmin;
    private boolean isDeleted;
    private boolean isSuspended;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
