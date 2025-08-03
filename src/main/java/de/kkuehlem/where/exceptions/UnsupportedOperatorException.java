package de.kkuehlem.where.exceptions;

import de.kkuehlem.where.definitions.WhereTypeDefinition;
import de.kkuehlem.where.parser.Operator;

public class UnsupportedOperatorException extends RuntimeException {

    public UnsupportedOperatorException(WhereTypeDefinition type, Operator operator) {
        super(String.format("Unsupported operator '%s' (%s) for type %s", 
                operator.getSymbol(), operator.name(), type.getName()));
    }

}
