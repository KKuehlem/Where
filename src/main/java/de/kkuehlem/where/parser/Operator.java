package de.kkuehlem.where.parser;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum Operator {
    EQUALS("="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">=");

    private final String symbol;

    private Operator(String symbol) {
        this.symbol = symbol;
    }
    
    private static final Map<String, Operator> map;
    static {
        map = new HashMap<>();
        for (Operator o : values()) {
            map.put(o.getSymbol(), o);
        }
    }
    
    public static Operator forSymbol(String symbol) {
        Operator o = map.get(symbol);
        if (o == null) throw new IllegalStateException("No such operator for symbol: " + symbol);
        else return o;
    }
}
