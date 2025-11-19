package de.kkuehlem.where.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class EvaluationException extends RuntimeException {

    private static final int PADDING = 7;

    public EvaluationException(String input, ParserRuleContext ctx, Throwable cause) {
        super(constructMessage(input, ctx, cause), cause);
    }

    private static String constructMessage(String input, ParserRuleContext ctx, Throwable cause) {
        int exprStart = ctx.getStart().getStartIndex();
        int exprStop = ctx.getStop().getStopIndex();

        int start = Math.max(0, exprStart - PADDING);
        int stop = Math.min(input.length(), exprStop + PADDING + 1);

        StringBuilder s = new StringBuilder();
        s.append("Error evaluating expression '");
        
        if (start < exprStart) s.append("...");
        s.append(input, start, exprStart);
        s.append(" >");
        s.append(input, exprStart, exprStop + 1);
        s.append("< ");
        s.append(input, exprStop + 1, stop);
        if (stop > exprStop + 1) s.append("...");
        s.append("'");

        if (cause != null && cause.getMessage() != null) {
            s.append(" - ").append(cause.getMessage());
        }
        return s.toString();
    }

}
