package de.kkuehlem.where.definitions;

import de.kkuehlem.where.context.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public abstract class WhereTypeDefinition<T> {
    
    protected final String name;
    protected final List<Class<? extends T>> supportedTypes;
    
    public boolean supports(@NonNull Class<? extends Object> check) {
        
        return supportedTypes.stream()
                .anyMatch(c -> check.isAssignableFrom(c)); // c is a super type of check
    }
    
    public abstract <O extends T> O parseLiteral(@NonNull String literal);
    
    public abstract <A extends T, B extends T> boolean evaluate(@NonNull A a, @NonNull Operator operator, @NonNull B b) throws UnsupportedOperatorException;

}
