package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ObjectIdentifierResolver implements IdentifierResolver {
    
    @NonNull private final Object object;

    @Override
    public Object resolveIdentifier(String name) throws NoSuchIdentifierException {
        try {
            Class<?> c = object.getClass();
            Field field = c.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        }
        catch (NoSuchFieldException ex) {
            throw new NoSuchIdentifierException(name);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
