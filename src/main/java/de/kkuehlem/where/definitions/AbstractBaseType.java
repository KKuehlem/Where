package de.kkuehlem.where.definitions;

import lombok.NonNull;

/**
 * Defines a where type which is "basic" in the sense that it can be parsed from literal constants. These types are handled different to {@link AbstractCustomType}s in the context, as they have special "places" to fill (e.g. for strings and numbers)
 * @author Konstantin Kühlem
 * @param <T> The /super) type of this definition
 */
public abstract class AbstractBaseType<T> extends AbstractType<T> {

    public AbstractBaseType(String name) {
        super(name);
    }
    
    public abstract T parseLiteral(@NonNull String literal);

}
