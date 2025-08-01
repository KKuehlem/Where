package de.kkuehlem.where.parser;

import de.kkuehlem.where.context.EvaluationContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class ExpressionEvaluator extends ExpressionBaseVisitor<Boolean> {
    
    @NonNull private final EvaluationContext context;

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
}
