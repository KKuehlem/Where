package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.LiteralParseException;
import de.kkuehlem.where.exceptions.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.List;

public class DateType extends AbstractCustomType<Temporal> {

    public DateType() {
        super("Date", List.of(
                LocalDate.class,
                LocalDateTime.class
        ));
    }

    @Override
    public Temporal fromString(String literal) throws LiteralParseException {
        try {
            return LocalDate.parse(literal);
        } catch (DateTimeParseException ex) {
            throw new LiteralParseException("Unable to parse date - date should be given as  ISO-8601 (yyyy-MM-dd)", ex);
        }
    }

    @Override
    public <A extends Temporal, B extends Temporal> boolean evaluate(A ia, Operator operator, B ib) throws UnsupportedOperatorException {
        LocalDateTime a = toLocalDate(ia);
        LocalDateTime b = toLocalDate(ib);

        switch (operator) {
            case EQUALS:
                return a.equals(b);
            case NOT_EQUALS:
                return !a.equals(b);
            case LESS_THAN:
                return a.isBefore(b);
            case LESS_THAN_OR_EQUALS:
                return a.isBefore(b) || a.equals(b);
            case GREATER_THAN:
                return a.isAfter(b);
            case GREATER_THAN_OR_EQUALS:
                return a.isAfter(b) || a.equals(b);
            default:
                throw new UnsupportedOperatorException(this, operator);
        }
    }

    private LocalDateTime toLocalDate(Temporal t) {
        if (t instanceof LocalDateTime d) return d;
        else if (t instanceof LocalDate d) return d.atStartOfDay();
        else
            throw new IllegalStateException("Unsupported temporal types: " + t.getClass().getCanonicalName());
    }

}
