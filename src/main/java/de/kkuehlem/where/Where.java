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

    public static <T> Predicate<T> where(String input) {
        return where(input, t -> WhereContext.builder()
                .resolver(new ObjectIdentifierResolver(t))
                .build()
        );
    }

    public static <T> Predicate<T> where(String input, Function<T, WhereContext> ctx) {
        ParseTree tree = createTree(input);

        return t -> new ExpressionEvaluator(ctx.apply(t), input).visit(tree);
    }

    public static boolean where(String input, WhereContext ctx) {
        
        return new ExpressionEvaluator(ctx, input)
                .visit(createTree(input));
    }
    
    private static ParseTree createTree(String input) {
        ExpressionLexer lexer = new ExpressionLexer(CharStreams.fromString(input));
        ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
        return parser.expression();
    }
}
