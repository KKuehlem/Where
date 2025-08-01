package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;


public class WhereTest {

    @Test
    public void stringTest() {
        Map<String, Object> map = Map.of(
                "x", "123"
        );
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();
        
        assertTrue(Where.where("x = '123'", ctx));
    }
}
