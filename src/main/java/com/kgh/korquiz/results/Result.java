package com.kgh.korquiz.results;

public interface Result {
    String name();

    default String nameToLower() {
        return this.name().toLowerCase();
    }
}
