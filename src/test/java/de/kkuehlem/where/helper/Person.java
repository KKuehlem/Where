package de.kkuehlem.where.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class Person {

    private final String name;
    private final int age;
}
