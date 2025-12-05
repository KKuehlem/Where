package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ObjectIdentifierResolver implements IdentifierResolver {

    private final Object object;
    private final Map<Class<?>, Map<String, Function<?, Object>>> additionalFields = new HashMap();

    public ObjectIdentifierResolver(Object object) {
        this(object, true);
    }

    public ObjectIdentifierResolver(Object object, boolean withDefaultAdditionFields) {
        this.object = object;

        withDefaultAdditionalFields();
    }

    /**
     * Register an additional fields, which is not part of a class but should be
     * treated as it would. If this field really exists, the additional field
     * will override it.
     *
     * @param <T>       the type of where the field should be added
     * @param cls       The class instance for the type
     * @param fieldName The name of the field
     * @param resolver  A function resolving an object for the fieldName
     *
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

    public void withDefaultAdditionalFields() {
        addAdditionalField(String.class, "length", s -> s.length());

        List<Class<? extends Temporal>> dateLikeClasses = List.of(
                LocalDate.class, LocalDateTime.class,
                ZonedDateTime.class, OffsetDateTime.class
        );
        
        dateLikeClasses.forEach(cls -> {
            addAdditionalField(cls, "year", d -> d.get(ChronoField.YEAR));
            addAdditionalField(cls, "month", d -> d.get(ChronoField.MONTH_OF_YEAR));
            addAdditionalField(cls, "day", d -> d.get(ChronoField.DAY_OF_MONTH));
        });
        
        List<Class<? extends Temporal>> timeLikeClasses = List.of(
                LocalDateTime.class,
                ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class
        );
        
        timeLikeClasses.forEach(cls -> {
            addAdditionalField(cls, "hour", d -> d.get(ChronoField.HOUR_OF_DAY));
            addAdditionalField(cls, "minute", d -> d.get(ChronoField.MINUTE_OF_HOUR));
            addAdditionalField(cls, "second", d -> d.get(ChronoField.SECOND_OF_MINUTE));
        });
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
