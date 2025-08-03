package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberType extends AbstractBaseType<Number> {

    public NumberType() {
        super("Number", List.of(
                Byte.class,
                Short.class,
                Integer.class,
                Long.class,
                Float.class,
                Double.class,
                AtomicInteger.class,
                AtomicLong.class,
                BigInteger.class,
                BigDecimal.class
        ));
    }

    @Override
    public Number parseLiteral(String literal) {
        return Double.valueOf(literal);
    }

    @Override
    public <A extends Number, B extends Number> boolean evaluate(A na, Operator operator, B nb) throws UnsupportedOperatorException {
        double a = na.doubleValue();
        double b = nb.doubleValue();

        switch (operator) {
            case EQUALS:
                return equals(a, b);
            case NOT_EQUALS:
                return !equals(a, b);
            case GREATER_THAN:
                return a > b && !equals(a, b);
            case GREATER_THAN_OR_EQUALS:
                return a > b || equals(a, b);
            case LESS_THAN:
                return a < b && !equals(a, b);
            case LESS_THAN_OR_EQUALS:
                return a < b || equals(a, b);
            default:
                throw new UnsupportedOperatorException(this, operator);
        }
    }

    private static boolean equals(double a, double b) {
        return Math.abs(a - b) < 0.0001;
    }

}
