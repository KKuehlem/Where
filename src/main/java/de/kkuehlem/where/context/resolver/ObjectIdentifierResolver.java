package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ObjectIdentifierResolver implements IdentifierResolver {

    @NonNull private final Object object;
    private final Map<Class<?>, Map<String, Function<?, Object>>> additionalFields = new HashMap();

    /**
     * Register an additional fields, which is not part of a class but should be
     * treated as it would. If this field really exists, the additional field
     * will override it.
     *
     * @param <T> the type of where the field should be added
     * @param cls The class instance for the type
     * @param fieldName The name of the field
     * @param resolver A function resolving an object for the fieldName
     * @return this, for chain calls
     */
    public <T> ObjectIdentifierResolver addAdditionalField(Class<T> cls, String fieldName, Function<T, Object> resolver) {
        Map<String, Function<?, Object>> classMap = additionalFields.get(cls);
        if (classMap == null) {
            classMap = new HashMap<>();
            additionalFields.put(cls, classMap);
        }
        classMap.put(fieldName, resolver);
        
        return this;
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
    
    private Object resolve(Object object, String name) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(name);
        
        // Check whether there is an additional field registered
        Map<String, Function<?, Object>> classMap = additionalFields.get(object.getClass());
        if (classMap != null) {
            Function<Object, Object> resolver = (Function<Object, Object>) classMap.get(name);
            if (resolver != null) {
                return resolver.apply(object);
            }
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
