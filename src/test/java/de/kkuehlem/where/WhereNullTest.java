package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class WhereNullTest {

    @Test
    public void testConversation() {
        Map<String, Object> map = new HashMap<>();
        map.put("n", null);

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("n = n", ctx));
        assertTrue(Where.where("n = null", ctx));
        assertTrue(Where.where("n.asInt = null", ctx));
        assertTrue(Where.where("n.asDouble = null", ctx));
        assertTrue(Where.where("n.asString = null", ctx));
        assertTrue(Where.where("n.x = null", ctx));
    }

}
