package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.additionalFields.DefaultAdditionalFields;
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

    @Test
    public void additionalFieldTest() {
        Book b = Book.builder()
                .title("The Title")
                .author(Person.builder()
                        .name("The Author")
                        .age(42)
                        .build()
                )
                .build();

        WhereContext ctx = WhereContext.builder()
                .resolver(new ObjectIdentifierResolver(b, new DefaultAdditionalFields()
                        .addAdditionalField(Book.class, "title", x -> "Another Title")
                        .addAdditionalField(Book.class, "otherFieldString", x -> "Some string")
                        .addAdditionalField(Book.class, "otherFieldNumber", x -> 42)
                        .addAdditionalField(Person.class, "name", x -> "Another Author")
                        .addAdditionalField(Person.class, "otherFieldNumber", x -> 123)
                ))
                .build();

        assertTrue(Where.where("author.age = 42", ctx)); // Normal nested field
        assertTrue(Where.where("title != 'The Title'", ctx)); // Overriden with additional field
        assertTrue(Where.where("title = 'Another Title'", ctx)); // Overriden with additional field
        assertTrue(Where.where("author.name = 'Another Author'", ctx)); // Overriden with additional field

        // New fields
        assertTrue(Where.where("otherFieldString = 'Some string'", ctx));
        assertTrue(Where.where("otherFieldNumber = 42", ctx));
        assertTrue(Where.where("otherFieldNumber > 41", ctx));
        assertTrue(Where.where("otherFieldNumber < 43", ctx));
        assertTrue(Where.where("author.otherFieldNumber = 123", ctx));
        assertTrue(Where.where("author.otherFieldNumber > 122", ctx));
        assertTrue(Where.where("author.otherFieldNumber < 124", ctx));
    }

}
