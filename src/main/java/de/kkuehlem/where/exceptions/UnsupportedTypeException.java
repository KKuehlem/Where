package de.kkuehlem.where.exceptions;

public class UnsupportedTypeException extends RuntimeException {

    public UnsupportedTypeException(Class<?> cls) {
        super("No definition for java type: " + cls.getCanonicalName());
    }

}
