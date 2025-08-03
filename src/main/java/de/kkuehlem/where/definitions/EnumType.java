package de.kkuehlem.where.definitions;

public class EnumType extends StringType {

    public EnumType() {
        super("Enum");
    }

    @Override
    public boolean supports(Class<? extends Object> check) {
        return check.isEnum() || super.supports(check);
    }

    @Override
    public CharSequence transformValue(Object value) {
        if (value instanceof Enum<?> e) return e.toString();
        else
            throw new IllegalStateException("Expected value to be an enum value:" + value.getClass().getCanonicalName());
    }

}
