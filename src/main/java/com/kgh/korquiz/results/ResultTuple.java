package com.kgh.korquiz.results;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResultTuple<T> {
    private T payload;
    private Result result;
}
