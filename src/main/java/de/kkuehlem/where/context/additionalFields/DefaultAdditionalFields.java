package de.kkuehlem.where.context.additionalFields;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultAdditionalFields extends AdditionalFields {

    protected final Map<String, Function<? extends CharSequence, Object>> charSequenceMap = new HashMap<>();
    protected final Map<String, Function<? extends Temporal, Object>> temporalMap = new HashMap<>();
    protected final Map<String, Function<? extends Number, Object>> numberMap = new HashMap<>();

    public DefaultAdditionalFields() {
        charSequenceMap.put("length", s -> s.length());
        charSequenceMap.put("asInt", s -> parse(s.toString(), Integer::valueOf));
        charSequenceMap.put("asDouble", s -> parse(s.toString(), Double::valueOf));

        temporalMap.put("year", d -> d.get(ChronoField.YEAR));
        temporalMap.put("month", d -> d.get(ChronoField.MONTH_OF_YEAR));
        temporalMap.put("day", d -> d.get(ChronoField.DAY_OF_MONTH));

        temporalMap.put("hour", d -> d.get(ChronoField.HOUR_OF_DAY));
        temporalMap.put("minute", d -> d.get(ChronoField.MINUTE_OF_HOUR));
        temporalMap.put("second", d -> d.get(ChronoField.SECOND_OF_MINUTE));
        
        numberMap.put("asString", n -> n.toString());
    }

    @Override
    public ResolvedField<?> resolve(Object object, String field) {
       ResolvedField<?> r = super.resolve(object, field);
        if (r.isCouldBeResolved()) return r;

        return super.resolve(object, field, getTypeMap(object));
    }

    private Map<String, Function<?, Object>> getTypeMap(Object object) {
        if (object instanceof CharSequence) return (Map) charSequenceMap;
        if (object instanceof Temporal) return (Map) temporalMap;
        if (object instanceof Number) return (Map) numberMap;
        return null;
    }
    
    private <A, B> B parse(A a, Function<A, B> parser) {
        try {
            return parser.apply(a);
        } catch (Exception ex) {
            return null;
        }
    }

}
