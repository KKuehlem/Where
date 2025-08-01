package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.MapIdentifierResolver;
import java.time.LocalDate;
import java.time.Month;
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
        assertFalse(Where.where("x = '1234'", ctx));
        assertTrue(Where.where("x = x OR x = '1234'", ctx));
        assertFalse(Where.where("x = x AND x = '1234'", ctx));
        assertTrue(Where.where("x = x AND (x = '1234' OR x = '123')", ctx));
    }
    
    @Test
    public void testNumbers() {
        Map<String, Object> map = Map.of(
                "a", 10,
                "b", 10.00000001, // Essential equal to a
                "c", 20
        );
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();
        
        assertTrue(Where.where("a = 10", ctx));
        assertTrue(Where.where("a >= 10", ctx));
        assertTrue(Where.where("a <= 10", ctx));
        
        assertTrue(Where.where("b = 10", ctx));
        assertTrue(Where.where("b >= 10", ctx));
        assertTrue(Where.where("b <= 10", ctx));
        
        assertTrue(Where.where("a = b", ctx));
        assertTrue(Where.where("a >= b", ctx));
        assertTrue(Where.where("a <= b", ctx));
        
        assertFalse(Where.where("a != b", ctx));
        assertFalse(Where.where("a < b", ctx));
        assertFalse(Where.where("a > b", ctx));
        
        assertTrue(Where.where("c = 20", ctx));
        assertTrue(Where.where("c > 19", ctx));
        assertTrue(Where.where("c > 19.5", ctx));
        assertTrue(Where.where("c > 19.99", ctx));
        assertTrue(Where.where("c < 21", ctx));
        assertTrue(Where.where("c < 21.5", ctx));
        assertTrue(Where.where("c < 21.99", ctx));
    }
    
    @Test
    public void dateTest() {
        Map<String, Object> map = Map.of(
                "a", LocalDate.of(2025, Month.FEBRUARY, 01)
        );
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();
        
        // Parsing is ISO Date -> Year-Month-Day
        assertTrue(Where.where("a = '2025-02-01'", ctx));
        assertTrue(Where.where("a >= '2025-02-01'", ctx));
        assertTrue(Where.where("a <= '2025-02-01'", ctx));
        
        assertTrue(Where.where("a < '2025-03-01'", ctx));
        assertTrue(Where.where("a > '2025-01-01'", ctx));
    }
    
    @Test
    public void negationTest() {
        Map<String, Object> map = Map.of(
                "x", "123"
        );
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();
        
        assertTrue(Where.where("NOT x = '1234'", ctx));
        assertTrue(Where.where("NOT x != '123'", ctx));
        assertTrue(Where.where("NOT (x != '123')", ctx));
        assertTrue(Where.where("NOT (NOT x = '123')", ctx));
        assertTrue(Where.where("NOT NOT x = '123'", ctx));
    }
    
    @Test
    public void booleanTest() {
        Map<String, Object> map = Map.of(
                "a", 10,
                "t", true,
                "f", false
        );
        
        WhereContext ctx = WhereContext.builder()
                .resolver(new MapIdentifierResolver(map))
                .build();
        
        assertTrue(Where.where("t", ctx));
        assertFalse(Where.where("NOT t", ctx));
        assertFalse(Where.where("f", ctx));
        assertTrue(Where.where("NOT f", ctx));
        assertTrue(Where.where("t OR f", ctx));
        assertFalse(Where.where("t AND f", ctx));
    }
}
