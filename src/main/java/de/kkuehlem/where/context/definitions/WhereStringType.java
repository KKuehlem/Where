package de.kkuehlem.where.context.definitions;

import de.kkuehlem.where.context.UnsupportedOperatorException;
import de.kkuehlem.where.parser.Operator;
import java.util.List;

public class WhereStringType extends WhereTypeDefinition<String> {

    public WhereStringType() {
        super("Text", List.of(
                String.class
        ));
    }

    @Override
    public <A extends String, B extends String> boolean evaluate(A a, Operator operator, B b) throws UnsupportedOperatorException {
        return false;
    }

}
