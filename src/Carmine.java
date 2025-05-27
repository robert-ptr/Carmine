import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Carmine {
    static ConstEnvironment constEnvironment = new ConstEnvironment(); // used for the first parse where we evaluate all arithmetic expressions
    static ModuleEnvironment moduleEnvironment = new ModuleEnvironment(); // used to save modules
    //static List<Stmt.Function> statements;

    static
    {
        constEnvironment.put("print", new CarmineCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(List<Object> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        constEnvironment.put("true", new CarmineCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(List<Object> arguments) {
                return null;
            }
        });

        constEnvironment.put("false", new CarmineCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(List<Object> arguments) {
                return null;
            }
        });
    }

    public static ConstEnvironment getEnvironment()
    {
        return constEnvironment;
    }

    public static void error(String message)
    {
        System.err.println(message);
    }
    public static void error(Token token, String message) { System.err.println(token.line + " " + message); }
    private static void run(String code)
    {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        /*
        for (Stmt statement : statements) {
            statement.print();
            //statement.evaluate();
            System.out.println();
        }
        */
        //Debug.printEnvironments();
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
    }
}