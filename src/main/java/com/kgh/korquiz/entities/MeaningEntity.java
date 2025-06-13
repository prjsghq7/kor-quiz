package com.kgh.korquiz.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "meaningId")
public class MeaningEntity {
    private int meaningId;
    private String targetCode;
    private int languageCode;
    private String languageName;
    private String definition;
    private int orderNo;
}
