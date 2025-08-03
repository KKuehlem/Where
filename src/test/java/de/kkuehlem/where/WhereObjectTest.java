package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.ObjectIdentifierResolver;
import de.kkuehlem.where.helper.Book;
import de.kkuehlem.where.helper.Person;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class WhereObjectTest {

    @Test
    public void basicTest() {
        Person p = Person.builder()
                .name("Peter")
                .age(42)
                .build();

        WhereContext ctx = WhereContext.builder()
                .resolver(new ObjectIdentifierResolver(p))
                .build();

        assertTrue(Where.where("age > 40 AND age < 50", ctx));
    }

    @Test
    public void nestedFieldstest() {
        Book b = Book.builder()
                .title("The Title")
                .author(Person.builder()
                        .name("The Author")
                        .age(42)
                        .build()
                )
                .build();
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new ObjectIdentifierResolver(b))
                .build();
        
        assertTrue(Where.where("author.age = 42", ctx));
        assertTrue(Where.where("author.age = author.age", ctx));
        assertTrue(Where.where("author.name != title", ctx));
    }

}
