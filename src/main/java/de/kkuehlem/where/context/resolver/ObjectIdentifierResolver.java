package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.context.additionalFields.AdditionalFields;
import de.kkuehlem.where.context.additionalFields.DefaultAdditionalFields;
import de.kkuehlem.where.context.additionalFields.ResolvedField;
import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.lang.reflect.Field;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectIdentifierResolver implements IdentifierResolver {

    private final Object object;
    private final AdditionalFields additionalFields;

    public ObjectIdentifierResolver(Object object) {
        this(object, true);
    }

    public ObjectIdentifierResolver(Object object, boolean withDefaultAdditionFields) {
        this(object, withDefaultAdditionFields ? new DefaultAdditionalFields() : null);
    }

    /**
     * Resolves an identifier as a field of object. This can also be a nested
     * field (e.g. person.age)
     *
     * @param name The name of the field, if nesting is used, fields of
     *             sub-objects are seperated with "."
     *
     * @return The value of the field of the object, can be null
     *
     * @throws NoSuchIdentifierException If no such field on this object or a
     *                                   sub-object exists
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

    protected Object resolve(Object object, String name) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(name);

        // Check whether there is an additional field registered
        if (additionalFields != null) {
            ResolvedField<?> r = additionalFields.resolve(object, name);
            if (r.isCouldBeResolved()) return r.getValue(); // Could still be null
        }

        return resolveWithReflection(object, name);
    }

    private static Object resolveWithReflection(Object object, String name) {
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
