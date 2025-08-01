package de.kkuehlem.where.context;

@FunctionalInterface
public interface IdentifierResolver {

    public Object resolveIdentifier(String name) throws NoSuchIdentifierException;
}
