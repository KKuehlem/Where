package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.List;
import java.util.Objects;

public class StringType extends AbstractBaseType<String> {

    public StringType() {
        super("Text", List.of(
                String.class
        ));
    }
    
    @Override
    public String parseLiteral(String literal) {
        if (!literal.startsWith("'")) throw new IllegalArgumentException("Malformed string literal: " + literal);
        if (!literal.endsWith("'")) throw new IllegalArgumentException("Malformed string literal: " + literal);
        
        return literal.substring(1, literal.length() - 1);
    }

    @Override
    public <A extends String, B extends String> boolean evaluate(A a, Operator operator, B b) throws UnsupportedOperatorException {
        switch (operator) {
            case EQUALS:
                return Objects.equals(a, b);
            case NOT_EQUALS:
                return !Objects.equals(a, b);
            default:
                throw new UnsupportedOperatorException(this, operator);
        }
    }

}
