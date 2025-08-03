package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * A abstract type definition for any custom types. This should either be {@link AbstractBaseWhereType}
 * @author Konstantin Kühlem
 * @param <T> The type of the highest super class for this type
 */
@AllArgsConstructor
@Getter
public abstract class AbstractType<T> {
    
    protected final String name;
    protected final List<Class<? extends T>> supportedTypes;
    
    public boolean supports(@NonNull Class<? extends Object> check) {
        
        return supportedTypes.stream()
                .anyMatch(c -> check.isAssignableFrom(c)); // c is a super type of check
    }
    
    /**
     * Evaluates a expression of "operand operator operand"
     * @param <A> The type of the left side
     * @param <B> The type of the right side
     * @param a The left side of the expression (never null)
     * @param operator The operator
     * @param b The right side of the expression (never null)
     * @return The result of the expression
     * @throws UnsupportedOperatorException if the operator is not supported for this type 
     */
    public abstract <A extends T, B extends T> boolean evaluate(@NonNull A a, @NonNull Operator operator, @NonNull B b) throws UnsupportedOperatorException;

}
