package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import de.kkuehlem.where.exceptions.BadEnumValueException;
import java.util.Map;
import static org.junit.Assert.*;
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
                .failOnBadEnumLiteral(true) // Also true by default
                .build();

        assertTrue(Where.where("a = a", ctx));
        assertTrue(Where.where("a = 'A'", ctx));
        assertTrue(Where.where("'A' = a", ctx));
        assertTrue(Where.where("a != b", ctx));
        assertTrue(Where.where("a != c", ctx));

        // 'D' is no constant in MyEnum, so this expression is useless
        assertThrows(BadEnumValueException.class, () -> Where.where("a = 'D'", ctx));
    }
}
