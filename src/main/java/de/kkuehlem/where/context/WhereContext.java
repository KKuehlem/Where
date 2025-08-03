package de.kkuehlem.where.context;

import de.kkuehlem.where.context.resolver.IdentifierResolver;
import de.kkuehlem.where.definitions.AbstractBaseType;
import de.kkuehlem.where.definitions.AbstractCustomType;
import de.kkuehlem.where.definitions.AbstractType;
import de.kkuehlem.where.definitions.DateType;
import de.kkuehlem.where.definitions.NumberType;
import de.kkuehlem.where.definitions.StringType;
import de.kkuehlem.where.exceptions.UnsupportedTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class WhereContext {

    public static final List<AbstractCustomType<?>> DEFAULT_CUSTOM_TYPES = List.of(
            new DateType()
    );

    private final IdentifierResolver resolver;
    private final List<AbstractCustomType<?>> customTypes;
    private final AbstractBaseType<String> stringType;
    private final AbstractBaseType<Number> numberType;
    @Getter(AccessLevel.NONE) private final Map<Class<?>, AbstractType<?>> typeLookup = new HashMap<>();

    @Builder
    public WhereContext(@NonNull IdentifierResolver resolver,
            AbstractBaseType<String> stringType,
            AbstractBaseType<Number> numberType,
            List<AbstractCustomType<?>> types) {

        this.resolver = resolver;
        this.customTypes = types != null ? types : DEFAULT_CUSTOM_TYPES;

        this.stringType = stringType != null ? stringType : new StringType();
        this.numberType = numberType != null ? numberType : new NumberType();

        for (AbstractCustomType<?> t : this.customTypes) {
            addSupportedTypes(t);
        }
        addSupportedTypes(this.numberType);
        addSupportedTypes(this.stringType);
    }

    public <T> AbstractType<? super T> getType(Class<T> cls) throws UnsupportedTypeException {
        AbstractType<? super T> d = (AbstractType<? super T>) typeLookup.get(cls);
        if (d == null) throw new UnsupportedTypeException(cls);
        else return d;
    }

    private void addSupportedTypes(AbstractType<?> t) {
        for (Class<?> cls : t.getSupportedTypes()) {
            AbstractType<?> old = typeLookup.put(cls, t);
            if (old != null) {
                throw new IllegalArgumentException(String.format("Duplicate definition for java class %s (defined in WhereTypeDefinition %s (%s) and in %s (%s))",
                        cls.getCanonicalName(), t.getName(), t.getClass().getCanonicalName(), old.getName(), old.getClass().getCanonicalName()));
            }
        }
    }

}
