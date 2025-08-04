package de.kkuehlem.where.context;

import de.kkuehlem.where.context.resolver.IdentifierResolver;
import de.kkuehlem.where.definitions.AbstractBaseType;
import de.kkuehlem.where.definitions.AbstractCustomType;
import de.kkuehlem.where.definitions.AbstractType;
import de.kkuehlem.where.definitions.DateType;
import de.kkuehlem.where.definitions.EnumType;
import de.kkuehlem.where.definitions.NumberType;
import de.kkuehlem.where.definitions.StringType;
import de.kkuehlem.where.exceptions.BadEnumValueException;
import de.kkuehlem.where.exceptions.UnsupportedTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

public class WhereContext {

    public static final List<AbstractCustomType<?>> DEFAULT_CUSTOM_TYPES = List.of(
            new DateType()
    );

    private final IdentifierResolver resolver;
    private final List<AbstractCustomType<?>> customTypes;
    @Getter private final AbstractBaseType<CharSequence> stringType;
    @Getter private final AbstractBaseType<Number> numberType;
    @Getter private final AbstractBaseType<CharSequence> enumType;
    /**
     * If enable, comparisons with bad enum literals will throw a {@link BadEnumValueException}
     * Default = true
     */
    @Getter private final boolean failOnBadEnumLiteral;
    private final List<AbstractBaseType<?>> baseTypes;
    private final Map<Class<?>, AbstractCustomType<?>> typeLookup = new HashMap<>();

    @Builder
    public WhereContext(@NonNull IdentifierResolver resolver,
            AbstractBaseType<CharSequence> stringType,
            AbstractBaseType<CharSequence> enumType,
            AbstractBaseType<Number> numberType,
            Boolean failOnBadEnumLiteral,
            List<AbstractCustomType<?>> types) {

        this.resolver = resolver;
        this.customTypes = types != null ? types : DEFAULT_CUSTOM_TYPES;

        this.stringType = stringType != null ? stringType : new StringType();
        this.enumType =  enumType != null ? enumType : new EnumType();
        this.numberType = numberType != null ? numberType : new NumberType();
        
        this.baseTypes = List.of(this.stringType, this.numberType, this.enumType);
        
        this.failOnBadEnumLiteral = failOnBadEnumLiteral != null ? failOnBadEnumLiteral : true;

        for (AbstractCustomType<?> t : this.customTypes) {
            for (Class<?> cls : t.getSupportedTypes()) {
                AbstractType<?> old = typeLookup.put(cls, t);
                if (old != null) {
                    throw new IllegalArgumentException(String.format("Duplicate definition for java class %s (defined in WhereTypeDefinition %s (%s) and in %s (%s))",
                            cls.getCanonicalName(), t.getName(), t.getClass().getCanonicalName(), old.getName(), old.getClass().getCanonicalName()));
                }
            }
        }
    }

    public <T> AbstractType<? super T> getType(Class<T> cls) throws UnsupportedTypeException {
        AbstractType<? super T> d = (AbstractType<? super T>) typeLookup.get(cls);
        if (d == null) {
            for (AbstractBaseType<?> type : baseTypes) {
                if (type.supports((Class<Object>) cls)) return (AbstractType<? super T>) type;
            }
            
            throw new UnsupportedTypeException(cls);
        }
        else return d;
    }

    public Object resolveIdentifier(String name) {
        if ("null".equals(name)) return null;
        else return resolver.resolveIdentifier(name);
    }

}
