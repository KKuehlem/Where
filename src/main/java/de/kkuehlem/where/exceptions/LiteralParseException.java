package de.kkuehlem.where.exceptions;

public class LiteralParseException extends RuntimeException {

    public LiteralParseException(String message) {
        super(message);
    }

    public LiteralParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
