package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class WhereEnumTest {

    enum MyEnum {
        A, B, C
    };

    @Test
    public void testEnums() {
        Map<String, Object> map = Map.of(
                "a", MyEnum.A,
                "b", MyEnum.B,
                "c", MyEnum.C
        );

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("a = a", ctx));
        assertTrue(Where.where("a = 'A'", ctx));
        assertTrue(Where.where("'A' = a", ctx));
        assertTrue(Where.where("a != b", ctx));
        assertTrue(Where.where("a != c", ctx));
    }
}
