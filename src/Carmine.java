import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Carmine {
    public static void error(String message)
    {
        System.err.println(message);
    }

    private static void run(String code)
    {
        System.out.println(code);
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }
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