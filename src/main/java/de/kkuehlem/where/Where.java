package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.parser.ExpressionEvaluator;
import de.kkuehlem.where.parser.ExpressionLexer;
import de.kkuehlem.where.parser.ExpressionParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Where {

    public static void main(String[] args) {
        WhereContext ctx = WhereContext.builder()
                .resolver(name -> "123")
                .build();
        
        String input = "x = '1234'";

        ExpressionLexer lexer = new ExpressionLexer(CharStreams.fromString(input));
        ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.expression();

        ExpressionEvaluator eval = new ExpressionEvaluator(ctx);
        System.out.println(input + " -> " + eval.visit(tree));
    }
}
