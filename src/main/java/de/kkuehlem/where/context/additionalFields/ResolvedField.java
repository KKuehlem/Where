package de.kkuehlem.where.context.additionalFields;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ResolvedField<T> {
    
    private final boolean couldBeResolved;
    private final T value;
    
    public static <T> ResolvedField<T> resolved(T value) {
        return new ResolvedField<>(true, value);
    }
    
    public static <T> ResolvedField<T> notResolved() {
        return new ResolvedField<>(false, null);
    }
    
}
