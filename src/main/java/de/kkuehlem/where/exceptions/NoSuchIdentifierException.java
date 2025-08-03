package de.kkuehlem.where.exceptions;

public class NoSuchIdentifierException extends RuntimeException {

    public NoSuchIdentifierException(String name) {
        super("No such identifier: " + name);
    }

}
