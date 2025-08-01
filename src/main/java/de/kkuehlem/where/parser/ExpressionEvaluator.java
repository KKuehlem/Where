package de.kkuehlem.where.parser;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.definitions.WhereTypeDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class ExpressionEvaluator extends ExpressionBaseVisitor<Boolean> {

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
        for (var exp : ctx.comparisonExpr()) {
            if (!visit(exp)) return false;
        }
        return true;
    }

    @Override
    public Boolean visitComparisonExpr(ExpressionParser.ComparisonExprContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }

        boolean leftIsIdentifier = ctx.operand(0).getStart().getType() == ExpressionLexer.IDENTIFIER;
        boolean rightIsIdentifier = ctx.operand(1).getStart().getType() == ExpressionLexer.IDENTIFIER;
        Operator operator = Operator.forSymbol(ctx.operator().getText());

        if (!leftIsIdentifier && !rightIsIdentifier) {
            throw new IllegalArgumentException("At least one operand has to be a identifier, comparing two liertal constants is not supported: " + ctx.getText());
        }

        Object left = null;
        Object right = null;
        WhereTypeDefinition<Object> type = null;

        // Collect identifiers and resolve type
        if (leftIsIdentifier) {
            left = context.getResolver().resolveIdentifier(ctx.operand(0).getText());

            if (left != null) {
                type = (WhereTypeDefinition<Object>) context.getType(left.getClass());
            }
        }
        if (rightIsIdentifier) {
            right = context.getResolver().resolveIdentifier(ctx.operand(1).getText());
            if (right != null) {
                if (type == null) { // Has not been resolved by left (because it is a literal or null)
                    type = (WhereTypeDefinition<Object>) context.getType(right.getClass());
                }
                else { // Was already infered from left -> Both sides are identifiers
                    if (!type.supports(right.getClass())) {
                        assert left != null; // Because type is set
                        
                        throw new IllegalArgumentException(String.format("Cannot compare %s and %s with %s", 
                                left.getClass().getCanonicalName(), right.getClass().getCanonicalName(), type.getClass().getCanonicalName()));
                    }
                }
            }
        }
        
        if (type == null) { // type can still be null, if both are identifiers and both resolve to null
            assert rightIsIdentifier && leftIsIdentifier && left == null && right == null;
            return solveNull(left, operator, right);
        }
        
        // Parse literals
        if (!leftIsIdentifier) left = type.parseLiteral(ctx.operand(0).getText());
        if (!rightIsIdentifier) right = type.parseLiteral(ctx.operand(1).getText());
        
        if (left == null || right == null) {
            return solveNull(left, operator, right);
        }

        return type.evaluate(left, operator, right);
    }

    // At least one is null
    private boolean solveNull(Object left, Operator operator, Object right) {
        if (left == null && right == null) {
            return operator == Operator.EQUALS; // Only true, if equals was the operator
        }
        
        // Only one is null
        return operator == Operator.NOT_EQUALS; // All other operators should be false 
    }

}
