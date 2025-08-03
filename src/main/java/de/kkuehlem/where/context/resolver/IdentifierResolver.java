package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;

@FunctionalInterface
public interface IdentifierResolver {

    public Object resolveIdentifier(String name) throws NoSuchIdentifierException;
}
