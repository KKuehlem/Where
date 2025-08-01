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
    protected final List<Class<?>> supportedTypes;
    
    public abstract <A extends T, B extends T> boolean evaluate(A a, Operator operator, B b) throws UnsupportedOperatorException;
}
