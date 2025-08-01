package de.kkuehlem.where.context;

public class NoSuchIdentifierException extends RuntimeException {

    public NoSuchIdentifierException(String name) {
        super("No such identifier: " + name);
    }

}
