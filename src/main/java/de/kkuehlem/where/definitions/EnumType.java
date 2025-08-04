package de.kkuehlem.where.definitions;

public class EnumType extends StringType {

    public EnumType() {
        super("Enum");
    }

    @Override
    public boolean supports(Class<? extends Object> check) {
        return check.isEnum() || super.supports(check);
    }

}
