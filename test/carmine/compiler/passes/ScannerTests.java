package carmine.compiler.passes;

import org.junit.jupiter.api.Test;

import carmine.compiler.structures.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTests {
    @Test
    void testModulesAndLogic() {
        String source = """
                module XOR(a, b) -> out
                {
                    out = and(or(a, b), not(and(a, b)));
                }

                module NAND(a, b) -> out
                {
                    out = not(and(a, b));
                }

                module operand1 = 1;
                module operand2 = 0;
                module output = XOR(NAND(operand1, operand2), 1);
                """;

        Scanner scanner = new Scanner(source);
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
                new Token(TokenType.AND, "and", null, 3),
                new Token(TokenType.LPAREN, "(", null, 3),
                new Token(TokenType.OR, "or", null, 3),
                new Token(TokenType.LPAREN, "(", null, 3),
                new Token(TokenType.IDENTIFIER, "a", null, 3),
                new Token(TokenType.COMMA, ",", null, 3),
                new Token(TokenType.IDENTIFIER, "b", null, 3),
                new Token(TokenType.RPAREN, ")", null, 3),
                new Token(TokenType.COMMA, ",", null, 3),
                new Token(TokenType.NOT, "not", null, 3),
                new Token(TokenType.LPAREN, "(", null, 3),
                new Token(TokenType.AND, "and", null, 3),
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
                new Token(TokenType.NOT, "not", null, 8),
                new Token(TokenType.LPAREN, "(", null, 8),
                new Token(TokenType.AND, "and", null, 8),
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
                new Token(TokenType.EOF, "", null, 14));

        assertEquals(expectedTokens.size(), tokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void testEmptyProgram() {
        String source = "";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        List<Token> expectedTokens = List.of(
                new Token(TokenType.EOF, "", null, 1));

        assertEquals(tokens.size(), expectedTokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void testBlockWithVariable() {
        String source = """
                {
                    var test = 0;
                }
                """;

        Scanner scanner = new Scanner(source);
        scanner.scanTokens();
    }

    @Test
    void testSimpleComment() {
        String source = "// hello";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        List<Token> expectedTokens = List.of(
                new Token(TokenType.EOF, "", null, 1));

        assertEquals(tokens.size(), expectedTokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void testArithmeticExpression() {
        String source = "(3 + 7 - (3 * 2)) / 4;";

        Scanner scanner = new Scanner(source);
        scanner.scanTokens();
    }

    @Test
    void testEnumAndVariables() {
        String source = """
                enum
                {
                    MIN = 5 + 3,
                    MAX = MIN + 10
                };

                3+7;

                var b = 7;
                var a = (-5 + 3 * (-2)) + b;
                """;

        Scanner scanner = new Scanner(source);
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
                new Token(TokenType.EOF, "", null, 11));

        assertEquals(expectedTokens.size(), tokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void testVariablesAndModules() {
        String source = """
                var b = -3 + 7 * 8 / (6 + 2);
                var a = b * 6 - c;
                var c = 78;

                module INV() -> out
                {
                }

                module AND3() -> out
                {
                }
                """;

        Scanner scanner = new Scanner(source);
        scanner.scanTokens();
    }

    @Test
    void testEmptyModules() {
        String source = """
                module INV() -> out
                {
                }

                module AND3() -> out
                {
                }
                """;

        Scanner scanner = new Scanner(source);
        scanner.scanTokens();
    }

    @Test
    void testComplexModulesWithLoops() {
        String source = """
                enum
                {
                    MAX = 10,
                    MIN = 2,
                    MEDIUM = 5,
                };

                var val = null;

                module xor10(a, b) -> out
                {
                    var i = 0;
                    while i < 10
                    {
                        out = and(or(a, b), not(and(a, b)));
                        i = i + 1;
                    }
                }

                module xorc(a, b, c) -> out
                {
                    if 3 + 4 > 5
                    {
                        out = and(or(a, b), not(and(a, b)));
                    }
                }

                module xor1(a, b) -> out
                {
                    out = and( or(a, b), not(and(a, b)));
                }
                """;

        Scanner scanner = new Scanner(source);
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

                new Token(TokenType.VAR, "var", null, 8),
                new Token(TokenType.IDENTIFIER, "val", null, 8),
                new Token(TokenType.ASSIGN, "=", null, 8),
                new Token(TokenType.NULL, "null", null, 8),
                new Token(TokenType.SEMICOLON, ";", null, 8),

                new Token(TokenType.MODULE, "module", null, 10),
                new Token(TokenType.IDENTIFIER, "xor10", null, 10),
                new Token(TokenType.LPAREN, "(", null, 10),
                new Token(TokenType.IDENTIFIER, "a", null, 10),
                new Token(TokenType.COMMA, ",", null, 10),
                new Token(TokenType.IDENTIFIER, "b", null, 10),
                new Token(TokenType.RPAREN, ")", null, 10),
                new Token(TokenType.ARROW, "->", null, 10),
                new Token(TokenType.IDENTIFIER, "out", null, 10),
                new Token(TokenType.LBRACE, "{", null, 11),
                new Token(TokenType.VAR, "var", null, 12),
                new Token(TokenType.IDENTIFIER, "i", null, 12),
                new Token(TokenType.ASSIGN, "=", null, 12),
                new Token(TokenType.DECIMAL, "0", 0, 12),
                new Token(TokenType.SEMICOLON, ";", null, 12),
                new Token(TokenType.WHILE, "while", null, 13),
                new Token(TokenType.IDENTIFIER, "i", null, 13),
                new Token(TokenType.LESS, "<", null, 13),
                new Token(TokenType.DECIMAL, "10", 10, 13),
                new Token(TokenType.LBRACE, "{", null, 14),
                new Token(TokenType.IDENTIFIER, "out", null, 15),
                new Token(TokenType.ASSIGN, "=", null, 15),
                new Token(TokenType.AND, "and", null, 15),
                new Token(TokenType.LPAREN, "(", null, 15),
                new Token(TokenType.OR, "or", null, 15),
                new Token(TokenType.LPAREN, "(", null, 15),
                new Token(TokenType.IDENTIFIER, "a", null, 15),
                new Token(TokenType.COMMA, ",", null, 15),
                new Token(TokenType.IDENTIFIER, "b", null, 15),
                new Token(TokenType.RPAREN, ")", null, 15),
                new Token(TokenType.COMMA, ",", null, 15),
                new Token(TokenType.NOT, "not", null, 15),
                new Token(TokenType.LPAREN, "(", null, 15),
                new Token(TokenType.AND, "and", null, 15),
                new Token(TokenType.LPAREN, "(", null, 15),
                new Token(TokenType.IDENTIFIER, "a", null, 15),
                new Token(TokenType.COMMA, ",", null, 15),
                new Token(TokenType.IDENTIFIER, "b", null, 15),
                new Token(TokenType.RPAREN, ")", null, 15),
                new Token(TokenType.RPAREN, ")", null, 15),
                new Token(TokenType.RPAREN, ")", null, 15),
                new Token(TokenType.SEMICOLON, ";", null, 15),
                new Token(TokenType.IDENTIFIER, "i", null, 16),
                new Token(TokenType.ASSIGN, "=", null, 16),
                new Token(TokenType.IDENTIFIER, "i", null, 16),
                new Token(TokenType.PLUS, "+", null, 16),
                new Token(TokenType.DECIMAL, "1", 1, 16),
                new Token(TokenType.SEMICOLON, ";", null, 16),
                new Token(TokenType.RBRACE, "}", null, 17),
                new Token(TokenType.RBRACE, "}", null, 18),

                new Token(TokenType.MODULE, "module", null, 20),
                new Token(TokenType.IDENTIFIER, "xorc", null, 20),
                new Token(TokenType.LPAREN, "(", null, 20),
                new Token(TokenType.IDENTIFIER, "a", null, 20),
                new Token(TokenType.COMMA, ",", null, 20),
                new Token(TokenType.IDENTIFIER, "b", null, 20),
                new Token(TokenType.COMMA, ",", null, 20),
                new Token(TokenType.IDENTIFIER, "c", null, 20),
                new Token(TokenType.RPAREN, ")", null, 20),
                new Token(TokenType.ARROW, "->", null, 20),
                new Token(TokenType.IDENTIFIER, "out", null, 20),
                new Token(TokenType.LBRACE, "{", null, 21),
                new Token(TokenType.IF, "if", null, 22),
                new Token(TokenType.DECIMAL, "3", 3, 22),
                new Token(TokenType.PLUS, "+", null, 22),
                new Token(TokenType.DECIMAL, "4", 4, 22),
                new Token(TokenType.GREATER, ">", null, 22),
                new Token(TokenType.DECIMAL, "5", 5, 22),
                new Token(TokenType.LBRACE, "{", null, 23),
                new Token(TokenType.IDENTIFIER, "out", null, 24),
                new Token(TokenType.ASSIGN, "=", null, 24),
                new Token(TokenType.AND, "and", null, 24),
                new Token(TokenType.LPAREN, "(", null, 24),
                new Token(TokenType.OR, "or", null, 24),
                new Token(TokenType.LPAREN, "(", null, 24),
                new Token(TokenType.IDENTIFIER, "a", null, 24),
                new Token(TokenType.COMMA, ",", null, 24),
                new Token(TokenType.IDENTIFIER, "b", null, 24),
                new Token(TokenType.RPAREN, ")", null, 24),
                new Token(TokenType.COMMA, ",", null, 24),
                new Token(TokenType.NOT, "not", null, 24),
                new Token(TokenType.LPAREN, "(", null, 24),
                new Token(TokenType.AND, "and", null, 24),
                new Token(TokenType.LPAREN, "(", null, 24),
                new Token(TokenType.IDENTIFIER, "a", null, 24),
                new Token(TokenType.COMMA, ",", null, 24),
                new Token(TokenType.IDENTIFIER, "b", null, 24),
                new Token(TokenType.RPAREN, ")", null, 24),
                new Token(TokenType.RPAREN, ")", null, 24),
                new Token(TokenType.RPAREN, ")", null, 24),
                new Token(TokenType.SEMICOLON, ";", null, 24),
                new Token(TokenType.RBRACE, "}", null, 25),
                new Token(TokenType.RBRACE, "}", null, 26),

                new Token(TokenType.MODULE, "module", null, 28),
                new Token(TokenType.IDENTIFIER, "xor1", null, 28),
                new Token(TokenType.LPAREN, "(", null, 28),
                new Token(TokenType.IDENTIFIER, "a", null, 28),
                new Token(TokenType.COMMA, ",", null, 28),
                new Token(TokenType.IDENTIFIER, "b", null, 28),
                new Token(TokenType.RPAREN, ")", null, 28),
                new Token(TokenType.ARROW, "->", null, 28),
                new Token(TokenType.IDENTIFIER, "out", null, 28),
                new Token(TokenType.LBRACE, "{", null, 29),
                new Token(TokenType.IDENTIFIER, "out", null, 30),
                new Token(TokenType.ASSIGN, "=", null, 30),
                new Token(TokenType.AND, "and", null, 30),
                new Token(TokenType.LPAREN, "(", null, 30),
                new Token(TokenType.OR, "or", null, 30),
                new Token(TokenType.LPAREN, "(", null, 30),
                new Token(TokenType.IDENTIFIER, "a", null, 30),
                new Token(TokenType.COMMA, ",", null, 30),
                new Token(TokenType.IDENTIFIER, "b", null, 30),
                new Token(TokenType.RPAREN, ")", null, 30),
                new Token(TokenType.COMMA, ",", null, 30),
                new Token(TokenType.NOT, "not", null, 30),
                new Token(TokenType.LPAREN, "(", null, 30),
                new Token(TokenType.AND, "and", null, 30),
                new Token(TokenType.LPAREN, "(", null, 30),
                new Token(TokenType.IDENTIFIER, "a", null, 30),
                new Token(TokenType.COMMA, ",", null, 30),
                new Token(TokenType.IDENTIFIER, "b", null, 30),
                new Token(TokenType.RPAREN, ")", null, 30),
                new Token(TokenType.RPAREN, ")", null, 30),
                new Token(TokenType.RPAREN, ")", null, 30),
                new Token(TokenType.SEMICOLON, ";", null, 30),
                new Token(TokenType.RBRACE, "}", null, 31),
                new Token(TokenType.EOF, "", null, 32));

        assertEquals(expectedTokens.size(), tokens.size());

        for (int i = 0; i < Math.min(tokens.size(), expectedTokens.size()); i++) {
            assertEquals(expectedTokens.get(i), tokens.get(i));
        }
    }

    @Test
    void testEnumIntensity() {
        String source = """
                enum INTENSITY
                {
                    MAX = 10,
                    MIN = 2,
                    MEDIUM = 5,
                };

                var val = null;
                module mod1 = null;
                """;

        Scanner scanner = new Scanner(source);
        scanner.scanTokens();
    }
}
