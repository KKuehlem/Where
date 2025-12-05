package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class WhereDateTest {


    @Test
    public void testDates() {
        Map<String, Object> map = Map.of(
                "date", LocalDate.of(2025, Month.DECEMBER, 5),
                "dateTime", LocalDate.of(2025, Month.DECEMBER, 5).atStartOfDay()
        );

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("date = '2025-12-05'", ctx));
        assertTrue(Where.where("date != '2026-12-05'", ctx));
        assertTrue(Where.where("date >= '2025-12-05'", ctx));
        assertTrue(Where.where("date <= '2025-12-05'", ctx));
        assertTrue(Where.where("date > '2025-12-04'", ctx));
        assertTrue(Where.where("date < '2025-12-06'", ctx));
        
        assertTrue(Where.where("dateTime = '2025-12-05'", ctx));
        assertTrue(Where.where("dateTime != '2026-12-05'", ctx));
        assertTrue(Where.where("dateTime >= '2025-12-05'", ctx));
        assertTrue(Where.where("dateTime <= '2025-12-05'", ctx));
        assertTrue(Where.where("dateTime > '2025-12-04'", ctx));
        assertTrue(Where.where("dateTime < '2025-12-06'", ctx));
    }
    
    @Test
    public void testAditionalFields() {
        Map<String, Object> map = Map.of(
                "date", LocalDate.of(2025, Month.DECEMBER, 5),
                "dateTime", LocalDate.of(2025, Month.DECEMBER, 5).atStartOfDay()
        );

        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();

        assertTrue(Where.where("date.year = 2025", ctx));
        assertTrue(Where.where("date.month = 12", ctx));
        assertTrue(Where.where("date.day = 5", ctx));
    }

}
