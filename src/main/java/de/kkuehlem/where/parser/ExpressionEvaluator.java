package de.kkuehlem.where.parser;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.definitions.AbstractBaseType;
import de.kkuehlem.where.definitions.AbstractCustomType;
import de.kkuehlem.where.definitions.AbstractType;
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

        AbstractType<Object> type;
        // Type if a custom type, if left or right are non-literal
        if (left.type() instanceof AbstractCustomType<?> c) {
            type = left.type();
            if (right.isLiteral())
                right = literalToCustomType(c, right.value());
            else checkSupportsComparison(type, right.value());
        }
        else {
            type = right.type();
            if (left.isLiteral() && type instanceof AbstractCustomType<?> c)
                left = literalToCustomType(c, left.value());
            else checkSupportsComparison(type, left.value());
        }

        // Evaluate using the computed type
        if (operator == Operator.NOT_EQUALS) {
            return !type.evaluate(left.value(), Operator.EQUALS, right.value());
        }
        else return type.evaluate(left.value(), operator, right.value());
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
        
        if (type != null) value = type.transformValue(value);

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

}
