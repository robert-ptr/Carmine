package carmine.compiler.passes;

import carmine.compiler.helpers.ASTVisualizer;
import carmine.compiler.helpers.VisualizationMode;
import carmine.compiler.structures.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Carmine {
    static Environment variableEnvironment = new Environment(); // used for the first parse where we evaluate all arithmetic expressions
    static Environment moduleEnvironment = new Environment(); // used to save modules
    //static List<Stmt.Function> statements;

    static
    {
        variableEnvironment.put("print", new CarmineCallable() {
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

        variableEnvironment.put("and", new CarmineCallable() {
           @Override
           public int arity() { return 2; }

           @Override
           public Object call(List<Object> arguments)
           {
               System.out.println(arguments.get(0));
               return null;
           }
        });

        variableEnvironment.put("or", new CarmineCallable() {
            @Override
            public int arity() { return 2; }

            @Override
            public Object call(List<Object> arguments)
            {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        moduleEnvironment.put("true", new CarmineCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(List<Object> arguments) {
                return null;
            }
        });

        moduleEnvironment.put("false", new CarmineCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(List<Object> arguments) {
                return null;
            }
        });

        variableEnvironment.put("import", new CarmineCallable() {
            @Override
            public int arity() { return 1;}

            @Override
            public Object call(List<Object> arguments)
            {
                return null;
            }
        });

        variableEnvironment.put("export", new CarmineCallable() {
            @Override
            public int arity() { return 1;}

            @Override
            public Object call(List<Object> arguments)
            {
                return null;
            }
        });
    }

    public static Environment getVarEnv()
    {
        return variableEnvironment;
    }

    public static Environment getModuleEnv()
    {
        return moduleEnvironment;
    }

    private static void run(String code)
    {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);

        System.out.println("________________\n");

        List<Stmt> statements = parser.parse();
        /*
        for (Stmt statement : statements) {
            //statement.print();
            //statement.evaluate();
            //System.out.println();
        }
        //Debug.printEnvironments();
         */

        ASTVisualizer visualizer = new ASTVisualizer();
        Optimizer optimizer = new Optimizer(statements);

        optimizer.constantFolding();
        //optimizer.constantPropagation(); // WIP
        optimizer.constantFolding();

        //optimizer.loopUnrolling(); // WIP
        //optimizer.deadCodeElimination(); // WIP

        String dotContent = visualizer.visualizeAST(statements, VisualizationMode.PRETTY_PRINT);
        System.out.println(dotContent);

        /*
        try {
            Files.writeString(
                    Path.of("graph.dot"),
                    dotContent,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            System.out.println("Saved to graph.dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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