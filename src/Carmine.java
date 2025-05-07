import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Carmine {
    static Environment environment = new Environment();

    static List<Stmt.Function> statements;

    public static Environment getEnvironment()
    {
        return environment;
    }

    public static void error(String message)
    {
        System.err.println(message);
    }

    private static void run(String code)
    {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();

        /*
        for (Token token : tokens)
        {
            System.out.println(token);
        }
         */

        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        for (Stmt statement : statements) {
            //statement.print();
            statement.evaluate();
            // System.out.println();
        }

        Debug.printEnvironments();
    }

    private static void runFile(String path) throws IOException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
        {
            System.out.println("Usage: carmine [script]");
            System.exit(2);
        }
        else
        {
            runFile(args[0]);
        }

        /*
        Expr expr = new Expr.Binary(
                new Expr.Group(
                    new Expr.Unary(new Token(TokenType.NOT, "not", null, 1), new Expr.Literal(true))
                ),
                new Token(TokenType.OR, "or", null, 1),
                new Expr.Literal(false));

        expr.print();
         */
    }
}