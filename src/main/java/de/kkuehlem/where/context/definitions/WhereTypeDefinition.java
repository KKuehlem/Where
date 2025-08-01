package de.kkuehlem.where.context.definitions;

import de.kkuehlem.where.context.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class WhereTypeDefinition<T> {
    
    protected final String name;
    protected final List<Class<? extends T>> supportedTypes;
    
    public boolean supports(Class<? extends Object> check) {
        
        return supportedTypes.stream()
                .anyMatch(c -> check.isAssignableFrom(c)); // c is a super type of check
    }
    
    public abstract <O extends T> O parseLiteral(String literal);
    
    public abstract <A extends T, B extends T> boolean evaluate(A a, Operator operator, B b) throws UnsupportedOperatorException;

}
