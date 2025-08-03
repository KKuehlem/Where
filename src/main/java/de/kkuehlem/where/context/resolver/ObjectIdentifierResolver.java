package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ObjectIdentifierResolver implements IdentifierResolver {
    
    @NonNull private final Object object;

    /**
     * Resolves an identifier as a field of object. This can also be a nested field (e.g. person.age)
     * @param name The name of the field, if nesting is used, fields of sub-objects are seperated with "." 
     * @return The value of the field of the object, can be null
     * @throws NoSuchIdentifierException If no such field on this object or a sub-object exists
     */
    @Override
    public Object resolveIdentifier(String name) throws NoSuchIdentifierException {
        Object o = object;
        for (String field : name.split("\\.")) {
            o = resolve(o, field);
            if (o == null) return null;
        }
        return o;
    }
    
    private static Object resolve(Object object, String name) {
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
