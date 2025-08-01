package de.kkuehlem.where.context.resolver;

import de.kkuehlem.where.context.NoSuchIdentifierException;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapIdentifierResolver implements IdentifierResolver {
    
    private final Map<String, ?> map;

    @Override
    public Object resolveIdentifier(String name) throws NoSuchIdentifierException {
        if (!map.containsKey(name)) throw new NoSuchIdentifierException(name);
        else return map.get(name); // Can still be null (if the variable is null)
    }

}
