package de.kkuehlem.where.context.additionalFields;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AdditionalFields {

    private final Map<Class<?>, Map<String, Function<?, Object>>> additionalFields = new HashMap();

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
    public <T> AdditionalFields addAdditionalField(Class<T> cls, String fieldName, Function<T, Object> resolver) {
        Map<String, Function<?, Object>> classMap = additionalFields.get(cls);
        if (classMap == null) {
            classMap = new HashMap<>();
            additionalFields.put(cls, classMap);
        }
        classMap.put(fieldName, resolver);

        return this;
    }

    public ResolvedField<?> resolve(Object object, String field) {
        return resolve(object, field, additionalFields.get(object.getClass()));
    }

    protected ResolvedField<?> resolve(Object object, String field, Map<String, Function<?, Object>> classMap) {
        if (classMap == null) return ResolvedField.notResolved();

        Function<Object, Object> resolver = (Function<Object, Object>) classMap.get(field);
        if (resolver == null) return ResolvedField.notResolved();
        
        return ResolvedField.resolved(resolver.apply(object));
    }

}
