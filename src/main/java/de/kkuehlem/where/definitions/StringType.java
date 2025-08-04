package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.Objects;

public class StringType extends AbstractBaseType<CharSequence> {
    
    public StringType(String name) {
        super(name);
    }

    public StringType() {
        super("Text");
    }

    @Override
    public String parseLiteral(String literal) {
        if (!literal.startsWith("'"))
            throw new IllegalArgumentException("Malformed string literal: " + literal);
        if (!literal.endsWith("'"))
            throw new IllegalArgumentException("Malformed string literal: " + literal);

        return literal.substring(1, literal.length() - 1);
    }

    @Override
    public boolean supports(Class<? extends Object> check) {
        return CharSequence.class.isAssignableFrom(check);
    }

    @Override
    public <A extends CharSequence, B extends CharSequence> boolean evaluate(A a, Operator operator, B b) throws UnsupportedOperatorException {
        switch (operator) {
            case EQUALS:
                return Objects.equals(a, b);
            default:
                throw new UnsupportedOperatorException(this, operator);
        }
    }

}
