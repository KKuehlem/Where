package de.kkuehlem.where;

import de.kkuehlem.where.helper.Person;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class WhereStreamTest {

    @Test
    public void testStream() {
        List<Person> persons = List.of(
                new Person("Anna", 32),
                new Person("Bert", 39),
                new Person("Peter", 42)
        );
        
        String input = "age <= 40";
        
        List<Person> filtered = persons.stream()
                .parallel()
                .filter(Where.filter(input))
                .toList();
        
        assertEquals(2, filtered.size());
    }
}
