package de.kkuehlem.where.definitions;

import java.util.List;
import lombok.NonNull;

/**
 * Defines a where type which is "basic" in the sense that it can be parsed from literal constants. These types are handled different to {@link AbstractCustomType}s in the context, as they have special "places" to fill (e.g. for strings and numbers)
 * @author Konstantin Kühlem
 * @param <T> The /super) type of this definition
 */
public abstract class AbstractBaseType<T> extends AbstractType<T> {

    public AbstractBaseType(String name, List<Class<? extends T>> supportedTypes) {
        super(name, supportedTypes);
    }
    
    public abstract T parseLiteral(@NonNull String literal);

}
