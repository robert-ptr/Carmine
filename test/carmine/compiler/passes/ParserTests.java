package carmine.compiler.passes;

import carmine.compiler.structures.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ParserTests {
    @Test
    void test0() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test0.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test1() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test1.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test2() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test2.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test3() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test3.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test4() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test4.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test5() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test5.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test6() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test6.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test7() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test7.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test8() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test8.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }

    @Test
    void test9() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test9.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }
}
