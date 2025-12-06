package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class WhereNumberTest {

    @Test
    public void testConversation() {
        Map<String, Object> map = Map.of(
                "a", 123,
                "b", "123",
                "c", "Not a number"
        );

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("a.asString = b", ctx));
        assertTrue(Where.where("a.asString = '123'", ctx));
        assertTrue(Where.where("a.asString.length = 3", ctx));
        assertTrue(Where.where("a.asString.length = b.length", ctx));
        assertTrue(Where.where("b.asInt = 123", ctx));
        assertTrue(Where.where("b.asDouble = 123", ctx));
        assertTrue(Where.where("b.asDouble >= 123", ctx));
        assertTrue(Where.where("b.asDouble > 122", ctx));
        assertTrue(Where.where("b.asDouble <= 123", ctx));
        assertTrue(Where.where("b.asDouble < 124", ctx));
        assertTrue(Where.where("a.asString.asInt = a", ctx));
        
        assertTrue(Where.where("a.asString CONTAINS '12'", ctx));
        assertTrue(Where.where("a.asString CONTAINS '123'", ctx));
        assertTrue(Where.where("a.asString MATCHES '\\d*'", ctx));
        assertTrue(Where.where("a.asString MATCHES '\\d+'", ctx));
        assertTrue(Where.where("a.asString MATCHES '\\d\\d\\d'", ctx));
        assertTrue(Where.where("NOT a.asString MATCHES '\\d\\d\\d\\d'", ctx));
        
        assertTrue(Where.where("c.asInt = null", ctx));
        assertTrue(Where.where("c.asDouble = null", ctx));
    }

}
