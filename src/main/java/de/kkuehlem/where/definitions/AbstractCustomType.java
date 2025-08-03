package de.kkuehlem.where.definitions;

import de.kkuehlem.where.exceptions.IllegalTypeException;
import de.kkuehlem.where.exceptions.LiteralParseException;
import java.util.List;

public abstract class AbstractCustomType<T> extends AbstractType<T> {

    public AbstractCustomType(String name, List<Class<? extends T>> supportedTypes) {
        super(name, supportedTypes);
    }
    
    /**
     * Parses a string literal constant to the type of this custom type
     * @param s The input string
     * @return A value of this custom type
     * @throws IllegalTypeException If parsing strings is not supported for this type
     * @throws LiteralParseException If the string does not represent a valid value of this custom type
     */
    public T fromString(String s) throws LiteralParseException {
        throw new IllegalTypeException("String values cannot be converted to " + name);
    }
    
    /**
     * Parses a numerical literal constant to the type of this custom type
     * @param n The input string
     * @return A value of this custom type
     * @throws IllegalTypeException If parsing numbers is not supported for this type
     * @throws LiteralParseException If the number does not represent a valid value of this custom type
     */
    public T fromNumber(Number n) throws LiteralParseException {
        throw new IllegalTypeException("Number values cannot be converted to " + name);
    }
}
