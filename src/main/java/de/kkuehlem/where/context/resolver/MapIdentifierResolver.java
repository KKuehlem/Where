package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.exceptions.NoSuchIdentifierException;
import java.util.Map;

public class MapIdentifierResolver extends ObjectIdentifierResolver {

    private final Map<String, ?> map;

    public MapIdentifierResolver(Map<String, ?> map) {
        this(map, true);
    }

    public MapIdentifierResolver(Map<String, ?> map, boolean withDefaultAdditionFields) {
        super(null, withDefaultAdditionFields);
        this.map = map;
    }

    @Override
    public Object resolveIdentifier(String name) throws NoSuchIdentifierException {
        String[] nameParts = name.split("\\.");

        if (!map.containsKey(nameParts[0]))
            throw new NoSuchIdentifierException(nameParts[0]);

        Object o = map.get(nameParts[0]); // Can still be null (if the variable is null)
        if (o == null) return null;

        for (int i = 1; i < nameParts.length; i++) {
            o = resolve(o, nameParts[i]);
            if (o == null) return null;
        }
        return o;
    }

}
