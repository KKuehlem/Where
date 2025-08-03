package de.kkuehlem.where.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class Book {
    
    private final String title;
    private final Person author;

}
