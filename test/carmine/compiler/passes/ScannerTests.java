package carmine.compiler.passes;

import org.junit.jupiter.api.Test;

import carmine.compiler.structures.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTests {
    @Test
    void test0() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test0.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();
        List<Token> expectedTokens = List.of(
            new Token(TokenType.MODULE, "module", null, 1),
            new Token(TokenType.IDENTIFIER, "XOR", null, 1),
            new Token(TokenType.LPAREN, "(", null, 1),
            new Token(TokenType.IDENTIFIER, "a", null, 1),
            new Token(TokenType.COMMA, ",", null, 1),
            new Token(TokenType.IDENTIFIER, "b", null, 1),
            new Token(TokenType.RPAREN, ")", null, 1),
            new Token(TokenType.ARROW, "->", null, 1),
            new Token(TokenType.IDENTIFIER, "out", null, 1),
            new Token(TokenType.LBRACE, "{", null, 2),
            new Token(TokenType.IDENTIFIER, "out", null, 3),
            new Token(TokenType.ASSIGN, "=", null, 3),
            new Token(TokenType.IDENTIFIER, "and", null, 3),
            new Token(TokenType.LPAREN, "(", null, 3),
            new Token(TokenType.IDENTIFIER, "or", null, 3),
            new Token(TokenType.LPAREN, "(", null, 3),
            new Token(TokenType.IDENTIFIER, "a", null, 3),
            new Token(TokenType.COMMA, ",", null, 3),
            new Token(TokenType.IDENTIFIER, "b", null, 3),
            new Token(TokenType.RPAREN, ")", null, 3),
            new Token(TokenType.COMMA, ",", null, 3),
            new Token(TokenType.IDENTIFIER, "not", null, 3),
            new Token(TokenType.LPAREN, "(", null, 3),
            new Token(TokenType.IDENTIFIER, "and", null, 3),
            new Token(TokenType.LPAREN, "(", null, 3),
            new Token(TokenType.IDENTIFIER, "a", null, 3),
            new Token(TokenType.COMMA, ",", null, 3),
            new Token(TokenType.IDENTIFIER, "b", null, 3),
            new Token(TokenType.RPAREN, ")", null, 3),
            new Token(TokenType.RPAREN, ")", null, 3),
            new Token(TokenType.RPAREN, ")", null, 3),
            new Token(TokenType.SEMICOLON, ";", null, 3),
            new Token(TokenType.RBRACE, "}", null, 4),

            new Token(TokenType.MODULE, "module", null, 6),
            new Token(TokenType.IDENTIFIER, "NAND", null, 6),
            new Token(TokenType.LPAREN, "(", null, 6),
            new Token(TokenType.IDENTIFIER, "a", null, 6),
            new Token(TokenType.COMMA, ",", null, 6),
            new Token(TokenType.IDENTIFIER, "b", null, 6),
            new Token(TokenType.RPAREN, ")", null, 6),
            new Token(TokenType.ARROW, "->", null, 6),
            new Token(TokenType.IDENTIFIER, "out", null, 6),
            new Token(TokenType.LBRACE, "{", null, 7),
            new Token(TokenType.IDENTIFIER, "out", null, 8),
            new Token(TokenType.ASSIGN, "=", null, 8),
            new Token(TokenType.IDENTIFIER, "not", null, 8),
            new Token(TokenType.LPAREN, "(", null, 8),
            new Token(TokenType.IDENTIFIER, "and", null, 8),
            new Token(TokenType.LPAREN, "(", null, 8),
            new Token(TokenType.IDENTIFIER, "a", null, 8),
            new Token(TokenType.COMMA, ",", null, 8),
            new Token(TokenType.IDENTIFIER, "b", null, 8),
            new Token(TokenType.RPAREN, ")", null, 8),
            new Token(TokenType.RPAREN, ")", null, 8),
            new Token(TokenType.SEMICOLON, ";", null, 8),
            new Token(TokenType.RBRACE, "}", null, 9),

            new Token(TokenType.MODULE, "module", null, 11),
            new Token(TokenType.IDENTIFIER, "operand1", null, 11),
            new Token(TokenType.ASSIGN, "=", null, 11),
            new Token(TokenType.DECIMAL, "1", 1, 11),
            new Token(TokenType.SEMICOLON, ";", null, 11),

            new Token(TokenType.MODULE, "module", null, 12),
            new Token(TokenType.IDENTIFIER, "operand2", null, 12),
            new Token(TokenType.ASSIGN, "=", null, 12),
            new Token(TokenType.DECIMAL, "0", 0, 12),
            new Token(TokenType.SEMICOLON, ";", null, 12),

            new Token(TokenType.MODULE, "module", null, 13),
            new Token(TokenType.IDENTIFIER, "output", null, 13),
            new Token(TokenType.ASSIGN, "=", null, 13),
            new Token(TokenType.IDENTIFIER, "XOR", null, 13),
            new Token(TokenType.LPAREN, "(", null, 13),
            new Token(TokenType.IDENTIFIER, "NAND", null, 13),
            new Token(TokenType.LPAREN, "(", null, 13),
            new Token(TokenType.IDENTIFIER, "operand1", null, 13),
            new Token(TokenType.COMMA, ",", null, 13),
            new Token(TokenType.IDENTIFIER, "operand2", null, 13),
            new Token(TokenType.RPAREN, ")", null, 13),
            new Token(TokenType.COMMA, ",", null, 13),
            new Token(TokenType.DECIMAL, "1", 1, 13),
            new Token(TokenType.RPAREN, ")", null, 13),
            new Token(TokenType.SEMICOLON, ";", null, 13),
            new Token(TokenType.EOF, "", null, 13)
        );

        assertEquals(tokens.size(), expectedTokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void test1() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test1.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test2() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test2.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test3() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test3.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test4() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test4.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test5() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test5.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();
        List<Token> expectedTokens = List.of(
            new Token(TokenType.ENUM, "enum", null, 1),
                new Token(TokenType.LBRACE, "{", null, 2),
                new Token(TokenType.IDENTIFIER, "MIN", null, 3),
                new Token(TokenType.ASSIGN, "=", null, 3),
                new Token(TokenType.DECIMAL, "5", 5, 3),
                new Token(TokenType.PLUS, "+", null, 3),
                new Token(TokenType.DECIMAL, "3", 3, 3),
                new Token(TokenType.COMMA, ",", null, 3),
                new Token(TokenType.IDENTIFIER, "MAX", null, 4),
                new Token(TokenType.ASSIGN, "=", null, 4),
                new Token(TokenType.IDENTIFIER, "MIN", null, 4),
                new Token(TokenType.PLUS, "+", null, 4),
                new Token(TokenType.DECIMAL, "10", 10, 4),
                new Token(TokenType.RBRACE, "}", null, 5),
                new Token(TokenType.SEMICOLON, ";", null, 5),

                new Token(TokenType.DECIMAL, "3", 3, 7),
                new Token(TokenType.PLUS, "+", null, 7),
                new Token(TokenType.DECIMAL, "7", 7, 7),
                new Token(TokenType.SEMICOLON, ";", null, 7),

                new Token(TokenType.VAR, "var", null, 9),
                new Token(TokenType.IDENTIFIER, "b", null, 9),
                new Token(TokenType.ASSIGN, "=", null, 9),
                new Token(TokenType.DECIMAL, "7", 7, 9),
                new Token(TokenType.SEMICOLON, ";", null, 9),

                new Token(TokenType.VAR, "var", null, 10),
                new Token(TokenType.IDENTIFIER, "a", null, 10),
                new Token(TokenType.ASSIGN, "=", null, 10),
                new Token(TokenType.LPAREN, "(", null, 10),
                new Token(TokenType.MINUS, "-", null, 10),
                new Token(TokenType.DECIMAL, "5", 5, 10),
                new Token(TokenType.PLUS, "+", null, 10),
                new Token(TokenType.DECIMAL, "3", 3, 10),
                new Token(TokenType.MUL, "*", null, 10),
                new Token(TokenType.LPAREN, "(", null, 10),
                new Token(TokenType.MINUS, "-", null, 10),
                new Token(TokenType.DECIMAL, "2", 2, 10),
                new Token(TokenType.RPAREN, ")", null, 10),
                new Token(TokenType.RPAREN, ")", null, 10),
                new Token(TokenType.PLUS, "+", null, 10),
                new Token(TokenType.IDENTIFIER, "b", null, 10),
                new Token(TokenType.SEMICOLON, ";", null, 10),
                new Token(TokenType.EOF, "", null, 10)
        );

        assertEquals(tokens.size(), expectedTokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void test6() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test6.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test7() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test7.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }

    @Test
    void test8() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test8.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();
        List<Token> expectedTokens = List.of(
            new Token(TokenType.ENUM, "enum", null, 1),
                new Token(TokenType.LBRACE, "{", null, 2),
                new Token(TokenType.IDENTIFIER, "MAX", null, 3),
                new Token(TokenType.ASSIGN, "=", null, 3),
                new Token(TokenType.DECIMAL, "10", 10, 3),
                new Token(TokenType.COMMA, ",", null, 3),
                new Token(TokenType.IDENTIFIER, "MIN", null, 4),
                new Token(TokenType.ASSIGN, "=", null, 4),
                new Token(TokenType.DECIMAL, "2", 2, 4),
                new Token(TokenType.COMMA, ",", null, 4),
                new Token(TokenType.IDENTIFIER, "MEDIUM", null, 5),
                new Token(TokenType.ASSIGN, "=", null, 5),
                new Token(TokenType.DECIMAL, "5", 5, 5),
                new Token(TokenType.COMMA, ",", null, 5),
                new Token(TokenType.RBRACE, "}", null, 6),
                new Token(TokenType.SEMICOLON, ";", null, 6),

                new Token(TokenType.VAR, "var", null, 7),
                new Token(TokenType.IDENTIFIER, "val", null, 7),
                new Token(TokenType.ASSIGN, "=", null,  7),
                new Token(TokenType.NULL, "null", null, 7),
                new Token(TokenType.SEMICOLON, ";", null, 7),

                new Token(TokenType.MODULE, "module", null, 8),
                new Token(TokenType.IDENTIFIER, "xor10", null, 8),
                new Token(TokenType.LPAREN, "(", null, 8),
                new Token(TokenType.IDENTIFIER, "a", null, 8),
                new Token(TokenType.COMMA, ",", null, 8),
                new Token(TokenType.IDENTIFIER, "b", null, 8),
                new Token(TokenType.RPAREN, ")", null, 8),
                new Token(TokenType.ARROW, "->", null, 8),
                new Token(TokenType.IDENTIFIER, "out", null, 8),
                new Token(TokenType.LBRACE, "{", null, 9),
                new Token(TokenType.VAR, "var", null, 10),
                new Token(TokenType.IDENTIFIER, "i", null, 10),
                new Token(TokenType.ASSIGN, "=", null, 10),
                new Token(TokenType.DECIMAL, "0", 0, 10),
                new Token(TokenType.SEMICOLON, ";", null, 10),
                new Token(TokenType.WHILE, "while", null, 11),
                new Token(TokenType.IDENTIFIER, "i", null, 11),
                new Token(TokenType.LESS, "<", null, 11),
                new Token(TokenType.DECIMAL, "10", 10, 11),
                new Token(TokenType.LBRACE, "{", null, 12),
                new Token(TokenType.IDENTIFIER, "out", null, 13),
                new Token(TokenType.ASSIGN, "=", null, 13),
                new Token(TokenType.IDENTIFIER, "and", null, 13),
                new Token(TokenType.LPAREN, "(", null, 13),
                new Token(TokenType.IDENTIFIER, "or", null, 13),
                new Token(TokenType.LPAREN, "(", null, 13),
                new Token(TokenType.IDENTIFIER, "a", null, 13),
                new Token(TokenType.COMMA, ",", null, 13),
                new Token(TokenType.IDENTIFIER, "b", null, 13),
                new Token(TokenType.RPAREN, ")", null, 13),
                new Token(TokenType.COMMA, ",", null, 13),
                new Token(TokenType.IDENTIFIER, "not", null, 13),
                new Token(TokenType.LPAREN, "(", null, 13),
                new Token(TokenType.IDENTIFIER, "and", null, 13),
                new Token(TokenType.LPAREN, "(", null, 13),
                new Token(TokenType.IDENTIFIER, "a", null, 13),
                new Token(TokenType.COMMA, ",", null, 13),
                new Token(TokenType.IDENTIFIER, "b", null, 13),
                new Token(TokenType.RPAREN, ")", null, 13),
                new Token(TokenType.RPAREN, ")", null, 13),
                new Token(TokenType.RPAREN, ")", null, 13),
                new Token(TokenType.SEMICOLON, ";", null, 13),
                new Token(TokenType.IDENTIFIER, "i", null, 14),
                new Token(TokenType.ASSIGN, "=", null, 14),
                new Token(TokenType.IDENTIFIER, "i", null, 14),
                new Token(TokenType.PLUS, "+", null, 14),
                new Token(TokenType.DECIMAL, "1", 1, 14),
                new Token(TokenType.SEMICOLON, ";", null, 14),
                new Token(TokenType.RBRACE, "}", null, 15),
                new Token(TokenType.RBRACE, "}", null, 16),

                new Token(TokenType.MODULE, "module", null, 17),
                new Token(TokenType.IDENTIFIER, "xorc", null, 17),
                new Token(TokenType.LPAREN, "(", null, 17),
                new Token(TokenType.IDENTIFIER, "a", null, 17),
                new Token(TokenType.COMMA, ",", null, 17),
                new Token(TokenType.IDENTIFIER, "b", null, 17),
                new Token(TokenType.COMMA, ",", null, 17),
                new Token(TokenType.IDENTIFIER, "c", null, 17),
                new Token(TokenType.RPAREN, ")", null, 17),
                new Token(TokenType.ARROW, "->", null, 17),
                new Token(TokenType.IDENTIFIER, "out", null, 17),
                new Token(TokenType.LBRACE, "{", null, 18),
                new Token(TokenType.IF, "if", null, 19),
                new Token(TokenType.DECIMAL, "3", 3, 19),
                new Token(TokenType.PLUS, "+", null, 19),
                new Token(TokenType.DECIMAL, "4", 4, 19),
                new Token(TokenType.GREATER, ">", null, 19),
                new Token(TokenType.DECIMAL, "5", 5, 19),
                new Token(TokenType.LBRACE, "{", null, 20),
                new Token(TokenType.IDENTIFIER, "out", null, 21),
                new Token(TokenType.ASSIGN, "=", null, 21),
                new Token(TokenType.IDENTIFIER, "and", null, 21),
                new Token(TokenType.LPAREN, "(", null, 21),
                new Token(TokenType.IDENTIFIER, "or", null, 21),
                new Token(TokenType.LPAREN, "(", null, 21),
                new Token(TokenType.IDENTIFIER, "a", null, 21),
                new Token(TokenType.COMMA, ",", null, 21),
                new Token(TokenType.IDENTIFIER, "b", null, 21),
                new Token(TokenType.RPAREN, ")", null, 21),
                new Token(TokenType.COMMA, ",", null, 21),
                new Token(TokenType.IDENTIFIER, "not", null, 21),
                new Token(TokenType.LPAREN, "(", null, 21),
                new Token(TokenType.IDENTIFIER, "and", null, 21),
                new Token(TokenType.LPAREN, "(", null, 21),
                new Token(TokenType.IDENTIFIER, "a", null, 21),
                new Token(TokenType.COMMA, ",", null, 21),
                new Token(TokenType.IDENTIFIER, "b", null, 21),
                new Token(TokenType.RPAREN, ")", null, 21),
                new Token(TokenType.RPAREN, ")", null, 21),
                new Token(TokenType.RPAREN, ")", null, 21),
                new Token(TokenType.SEMICOLON, ";", null, 21),
                new Token(TokenType.RBRACE, "}", null, 22),
                new Token(TokenType.RBRACE, "}", null, 23),

                new Token(TokenType.MODULE, "module", null, 24),
                new Token(TokenType.IDENTIFIER, "xor1", null, 24),
                new Token(TokenType.LPAREN, "(", null, 24),
                new Token(TokenType.IDENTIFIER, "a", null, 24),
                new Token(TokenType.COMMA, ",", null, 24),
                new Token(TokenType.IDENTIFIER, "b", null, 24),
                new Token(TokenType.RPAREN, ")", null, 24),
                new Token(TokenType.ARROW, "->", null, 24),
                new Token(TokenType.IDENTIFIER, "out", null, 24),
                new Token(TokenType.RBRACE, "{", null, 25),
                new Token(TokenType.IDENTIFIER, "out", null, 26),
                new Token(TokenType.ASSIGN, "=", null, 26),
                new Token(TokenType.IDENTIFIER, "and", null, 26),
                new Token(TokenType.LPAREN, "(", null, 26),
                new Token(TokenType.IDENTIFIER, "or", null, 26),
                new Token(TokenType.LPAREN, "(", null, 26),
                new Token(TokenType.IDENTIFIER, "a", null, 26),
                new Token(TokenType.COMMA, ",", null, 26),
                new Token(TokenType.IDENTIFIER, "b", null, 26),
                new Token(TokenType.RPAREN, ")", null, 26),
                new Token(TokenType.COMMA, ",", null, 26),
                new Token(TokenType.IDENTIFIER, "not", null, 26),
                new Token(TokenType.LPAREN, "(", null, 26),
                new Token(TokenType.IDENTIFIER, "and", null, 26),
                new Token(TokenType.LPAREN, "(", null, 26),
                new Token(TokenType.IDENTIFIER, "a", null, 26),
                new Token(TokenType.COMMA, ",", null, 26),
                new Token(TokenType.IDENTIFIER, "b", null, 26),
                new Token(TokenType.RPAREN, ")", null, 26),
                new Token(TokenType.RPAREN, ")", null, 26),
                new Token(TokenType.RPAREN, ")", null, 26),
                new Token(TokenType.SEMICOLON, ";", null, 26),
                new Token(TokenType.RBRACE, "}", null, 27),
                new Token(TokenType.EOF, "", null, 27)
        );

        assertEquals(tokens.size(), expectedTokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void test9() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test9.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        scanner.scanTokens();
    }
}
