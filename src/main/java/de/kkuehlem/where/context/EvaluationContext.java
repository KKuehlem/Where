package de.kkuehlem.where.context;

import de.kkuehlem.where.context.definitions.WhereStringType;
import de.kkuehlem.where.context.definitions.WhereTypeDefinition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class EvaluationContext {

    public static final List<WhereTypeDefinition<?>> DEFAULT_TYPES = List.of(
            new WhereStringType()
    );

    @NonNull private final IdentifierResolver resolver;
    private final List<WhereTypeDefinition<?>> types;
    private final Map<Class<?>, WhereTypeDefinition<?>> typeLookup = new HashMap<>();

    @Builder
    public EvaluationContext(@NonNull IdentifierResolver resolver, List<WhereTypeDefinition<?>> types) {
        this.resolver = resolver;
        this.types = types != null ? types : DEFAULT_TYPES;

        for (WhereTypeDefinition<?> t : this.types) {
            for (Class<?> cls : t.getSupportedTypes()) {
                WhereTypeDefinition<?> old = typeLookup.put(cls, t);
                if (old != null) {
                    throw new IllegalArgumentException(String.format("Duplicate definition for java class %s (defined in WhereTypeDefinition %s (%s) and in %s (%s))",
                            cls.getCanonicalName(), t.getName(), t.getClass().getCanonicalName(), old.getName(), old.getClass().getCanonicalName()));
                }
            }
        }
    }
}
