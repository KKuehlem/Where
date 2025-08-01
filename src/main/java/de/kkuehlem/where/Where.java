package de.kkuehlem.where;

import de.kkuehlem.where.context.WhereContext;
import de.kkuehlem.where.context.resolver.ObjectIdentifierResolver;
import de.kkuehlem.where.parser.ExpressionEvaluator;
import de.kkuehlem.where.parser.ExpressionLexer;
import de.kkuehlem.where.parser.ExpressionParser;
import java.util.function.Function;
import java.util.function.Predicate;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Where {

    public static <T> Predicate<T> filter(String input) {
        return filter(input, t -> WhereContext.builder()
                .resolver(new ObjectIdentifierResolver(t))
                .build()
        );
    }

    public static <T> Predicate<T> filter(String input, Function<T, WhereContext> ctx) {
        ExpressionLexer lexer = new ExpressionLexer(CharStreams.fromString(input));
        ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.expression();

        return t -> new ExpressionEvaluator(ctx.apply(t)).visit(tree);
    }

    public static boolean where(String input, WhereContext ctx) {
        ExpressionLexer lexer = new ExpressionLexer(CharStreams.fromString(input));
        ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.expression();

        ExpressionEvaluator eval = new ExpressionEvaluator(ctx);
        return eval.visit(tree);
    }
}
