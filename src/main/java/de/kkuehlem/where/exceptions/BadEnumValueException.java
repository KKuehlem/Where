package de.kkuehlem.where.exceptions;

public class BadEnumValueException extends RuntimeException {

    public BadEnumValueException(String value, Class<?> cls) {
        super(String.format("No such enum constant '%s' in class %s. This expression can never be true", value, cls));
    }

}
