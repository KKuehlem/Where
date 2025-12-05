package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class WhereStringTest {


    @Test
    public void tesContains() {
        Map<String, Object> map = Map.of(
                "a", "a",
                "abc", "abc"
        );

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("abc CONTAINS 'a'", ctx));
        assertTrue(Where.where("abc CONTAINS a", ctx));
        assertFalse(Where.where("NOT abc CONTAINS a", ctx));
    }
    
    @Test
    public void testMatches() {
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(Map.of()))
                .build();

        assertTrue(Where.where("'abc' MATCHES 'abc'", ctx));
        assertTrue(Where.where("'abc' MATCHES '.*'", ctx));
        assertTrue(Where.where("'abc' MATCHES '.*c'", ctx));
        assertTrue(Where.where("'abc' MATCHES 'a.*c'", ctx));
        assertTrue(Where.where("NOT 'abc' MATCHES '.*d'", ctx));
        
        assertTrue(Where.where("'abc 123 def' MATCHES 'abc 123 def'", ctx));
        assertTrue(Where.where("'abc 123 def' MATCHES 'abc \\d+ def'", ctx));
        assertTrue(Where.where("'abc 123 def' MATCHES 'abc \\d* def'", ctx));
        assertTrue(Where.where("'abc 123 def' MATCHES 'abc \\d\\d\\d def'", ctx));
        assertTrue(Where.where("NOT 'abc 123 def' MATCHES 'abc \\d\\d def'", ctx));
    }
}
