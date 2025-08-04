package de.kkuehlem.where.parser;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.definitions.AbstractBaseType;
import de.kkuehlem.where.definitions.AbstractCustomType;
import de.kkuehlem.where.definitions.AbstractType;
import de.kkuehlem.where.exceptions.BadEnumValueException;
import de.kkuehlem.where.exceptions.EvaluationException;
import de.kkuehlem.where.exceptions.IllegalTypeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class ExpressionEvaluator extends ExpressionBaseVisitor<Boolean> {

    record TypeAndValue(AbstractType<Object> type, Object value, boolean isLiteral) {

    }

    @NonNull private final WhereContext context;
    @NonNull private final String originalInput;

    @Override
    public Boolean visitOrExpr(ExpressionParser.OrExprContext ctx) {
        for (var exp : ctx.andExpr()) {
            if (visit(exp)) return true;
        }
        return false;
    }

    @Override
    public Boolean visitAndExpr(ExpressionParser.AndExprContext ctx) {
        for (var exp : ctx.notExpr()) {
            if (!visit(exp)) return false;
        }
        return true;
    }

    @Override
    public Boolean visitNotExpr(ExpressionParser.NotExprContext ctx) {
        boolean isNot = ctx.getChildCount() == 2 && ctx.getChild(0).getText().equalsIgnoreCase("NOT");

        if (isNot) return !visit(ctx.notExpr());
        else return visit(ctx.booleanExpr());
    }
    @Override
    public Boolean visitBooleanExpr(ExpressionParser.BooleanExprContext ctx) {
        if (ctx.expression() != null) { // ( expression )
            return visit(ctx.expression());
        }
        if (ctx.qualifiedIdentifier() != null) { // Whole expression is a identifier 
            return resolveBoolean(ctx.qualifiedIdentifier().getText());
        }
        
        try {
            return visitBooleanInternal(ctx);
        } catch (RuntimeException ex) {
            throw new EvaluationException(originalInput, ctx, ex);
        }
    }

    public Boolean visitBooleanInternal(ExpressionParser.BooleanExprContext ctx) {
        Operator operator = Operator.forSymbol(ctx.operator().getText());

        // Collect identifiers and resolve type
        TypeAndValue left = parseOperand(ctx.operand(0));
        TypeAndValue right = parseOperand(ctx.operand(1));

        if (left.value() == null || right.value() == null) {
            // Null checks do not need the right types
            return solveNull(left.value(), operator, right.value());
        }

        // From here on, neither values nor types are null
        assert left.type() != null && right.type() != null;

        AbstractType<?> type = null;
        Class<?> enumClass = checkEnum(left, right);
        if (enumClass != null) {
            type = context.getEnumType();

            if (left.isLiteral())
                checkEnumValue(enumClass, left.value().toString());
            if (right.isLiteral())
                checkEnumValue(enumClass, right.value().toString());

            left = new TypeAndValue(null, left.value().toString(), left.isLiteral());
            right = new TypeAndValue(null, right.value().toString(), right.isLiteral());
        }
        else {
            // Type if a custom type, if left or right are non-literal
            if (left.type() instanceof AbstractCustomType<?> c) {
                type = (AbstractType<Object>) c;
                if (right.isLiteral()) { // Comparison with custom type and literal
                    right = literalToCustomType(c, right.value()); // (a)
                }
                else { // If right is literal, this check is not needed, because of the literalToCustom conversion
                    checkSupportsComparison(c, right.value());
                }
            }
            else {
                type = right.type(); // Use the right type no matter what
                if (left.isLiteral() && type instanceof AbstractCustomType<?> c) {
                    left = literalToCustomType(c, left.value()); // (b) Same as (a) but other way around
                }
                else checkSupportsComparison(type, left.value());
            }
        }

        // Just for typing
        AbstractType<Object> t = (AbstractType<Object>) type;

        // Evaluate using the computed type
        if (operator == Operator.NOT_EQUALS) {
            return !t.evaluate(left.value(), Operator.EQUALS, right.value());
        }
        else return t.evaluate(left.value(), operator, right.value());
    }

    private TypeAndValue parseOperand(ExpressionParser.OperandContext o) {
        Object value;
        AbstractType<Object> type = null;
        boolean isLiteral = o.qualifiedIdentifier() == null;

        if (!isLiteral) { // Operand is a identifier
            value = context.resolveIdentifier(o.getText());

            if (value != null) {
                type = (AbstractType<Object>) context.getType(value.getClass());
            }
        }
        else {
            AbstractBaseType<?> base;
            if (o.STRING() != null) base = context.getStringType();
            else if (o.NUMBER() != null) base = context.getNumberType();
            else throw new IllegalStateException();

            value = base.parseLiteral(o.getText());
            type = (AbstractType<Object>) base;
        }

        return new TypeAndValue(type, value, isLiteral);
    }

    // At least one is null
    private boolean solveNull(Object left, Operator operator, Object right) {
        if (left == null && right == null) {
            return operator == Operator.EQUALS
                    || operator == Operator.GREATER_THAN_OR_EQUALS
                    || operator == Operator.LESS_THAN_OR_EQUALS; // Only true, if a equals was the operator
        }

        // Only one is null
        return operator == Operator.NOT_EQUALS; // All other operators should be false 
    }

    private Boolean resolveBoolean(String name) {
        Object value = context.resolveIdentifier(name);
        if (value instanceof Boolean b) return b;
        else if (value == null) return false;
        else
            throw new IllegalTypeException("Expected boolean, got " + value.getClass().getCanonicalName());
    }

    private TypeAndValue literalToCustomType(AbstractCustomType<?> type, Object value) {
        if (value instanceof String s)
            return new TypeAndValue((AbstractType<Object>) type, type.fromString(s), false);
        else if (value instanceof Number n)
            return new TypeAndValue((AbstractType<Object>) type, type.fromNumber(n), false);
        else
            throw new IllegalStateException(value.getClass().getCanonicalName());
    }

    private void checkSupportsComparison(AbstractType<?> type, Object otherValue) {
        if (!type.supports(otherValue.getClass())) {
            throw new IllegalTypeException(String.format("Cannot compare %s with %s",
                    otherValue.getClass().getCanonicalName(), type.getClass().getCanonicalName()));
        }
    }

    private Class<?> checkEnum(TypeAndValue left, TypeAndValue right) {
        if (left.value().getClass().isEnum()) return left.value().getClass();
        else if (right.value().getClass().isEnum())
            return right.value().getClass();
        else return null;
    }

    private void checkEnumValue(Class<?> enumClass, String s) {
        if (!context.isFailOnBadEnumLiteral()) return;

        try {
            Enum.valueOf((Class<? extends Enum>) enumClass, s);
        }
        catch (IllegalArgumentException ex) {
            throw new BadEnumValueException(s, enumClass);
        }
    }
}
