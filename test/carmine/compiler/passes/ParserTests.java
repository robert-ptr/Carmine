package carmine.compiler.passes;

import carmine.compiler.structures.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// for now this only tests that parsing finishes without errors
// more complicated cases should be tested in the future, such as:
// running code with syntax errors and checking the errors
// going through the generated AST and ensuring it is as expected
public class ParserTests {
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

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(5, statements.size());

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(0));
        assertEquals("XOR", ((Stmt.ModuleFunction) statements.get(0)).name.getLexeme());

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(1));
        assertEquals("NAND", ((Stmt.ModuleFunction) statements.get(1)).name.getLexeme());

        assertInstanceOf(Stmt.Expression.class, statements.get(2));
        assertInstanceOf(Expr.Module.class, ((Stmt.Expression) statements.get(2)).expr);
        assertEquals("operand1", ((Expr.Module) ((Stmt.Expression) statements.get(2)).expr).getName().getLexeme());

        assertInstanceOf(Stmt.Expression.class, statements.get(3));
        assertInstanceOf(Expr.Module.class, ((Stmt.Expression) statements.get(3)).expr);
        assertEquals("operand2", ((Expr.Module) ((Stmt.Expression) statements.get(3)).expr).getName().getLexeme());

        assertInstanceOf(Stmt.Expression.class, statements.get(4));
        assertInstanceOf(Expr.Module.class, ((Stmt.Expression) statements.get(4)).expr);
        assertEquals("output", ((Expr.Module) ((Stmt.Expression) statements.get(4)).expr).getName().getLexeme());
    }

    @Test
    void testEmptyProgram() {
        String source = "";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // 0 statements expected
        assertEquals(0, statements.size());
    }

    @Test
    void testBlockWithVariable() {
        String source = """
                {
                    var test = 0;
                }
                """;

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // 1 block statement expected
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Block.class, statements.get(0));

        Stmt.Block block = (Stmt.Block) statements.get(0);
        assertEquals(1, block.statements.size());
        assertInstanceOf(Stmt.Expression.class, block.statements.get(0));
        // var test = 0; inside block is parsed as Expression(Variable)
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) block.statements.get(0)).expr);
    }

    @Test
    void testSimpleComment() {
        String source = "// hello";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(0, statements.size());
    }

    @Test
    void testArithmeticExpression() {
        String source = "(3 + 7 - (3 * 2)) / 4;";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.get(0));
        assertInstanceOf(Expr.Binary.class, ((Stmt.Expression) statements.get(0)).expr);
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

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(4, statements.size());
        assertInstanceOf(Stmt.Enum.class, statements.get(0));
        assertInstanceOf(Stmt.Expression.class, statements.get(1)); // 3+7

        assertInstanceOf(Stmt.Expression.class, statements.get(2));
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(2)).expr); // var b = 7

        assertInstanceOf(Stmt.Expression.class, statements.get(3));
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(3)).expr); // var a = ...
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
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // 5 statements expected
        assertEquals(5, statements.size());

        assertInstanceOf(Stmt.Expression.class, statements.get(0)); // var b
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(0)).expr);

        assertInstanceOf(Stmt.Expression.class, statements.get(1)); // var a
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(1)).expr);

        assertInstanceOf(Stmt.Expression.class, statements.get(2)); // var c
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(2)).expr);

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(3)); // INV
        assertEquals("INV", ((Stmt.ModuleFunction) statements.get(3)).name.getLexeme());

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(4)); // AND3
        assertEquals("AND3", ((Stmt.ModuleFunction) statements.get(4)).name.getLexeme());
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
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(2, statements.size());

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(0));
        assertEquals("INV", ((Stmt.ModuleFunction) statements.get(0)).name.getLexeme());

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(1));
        assertEquals("AND3", ((Stmt.ModuleFunction) statements.get(1)).name.getLexeme());
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

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(5, statements.size());

        assertInstanceOf(Stmt.Enum.class, statements.get(0));

        assertInstanceOf(Stmt.Expression.class, statements.get(1)); // var val
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(1)).expr);

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(2)); // xor10
        Stmt.ModuleFunction xor10 = (Stmt.ModuleFunction) statements.get(2);
        assertEquals("xor10", xor10.name.getLexeme());
        // Verify Check while loop inside xor10
        // xor10 statements: var i=0; while...
        assertEquals(2, xor10.statements.statements.size());
        assertInstanceOf(Stmt.While.class, xor10.statements.statements.get(1));

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(3)); // xorc
        Stmt.ModuleFunction xorc = (Stmt.ModuleFunction) statements.get(3);
        assertEquals("xorc", xorc.name.getLexeme());
        // Verify If inside xorc
        assertEquals(1, xorc.statements.statements.size());
        assertInstanceOf(Stmt.If.class, xorc.statements.statements.get(0));

        assertInstanceOf(Stmt.ModuleFunction.class, statements.get(4)); // xor1
        assertEquals("xor1", ((Stmt.ModuleFunction) statements.get(4)).name.getLexeme());
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
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        assertEquals(3, statements.size());

        assertInstanceOf(Stmt.Enum.class, statements.get(0));

        assertInstanceOf(Stmt.Expression.class, statements.get(1)); // var val
        assertInstanceOf(Expr.Variable.class, ((Stmt.Expression) statements.get(1)).expr);

        assertInstanceOf(Stmt.Expression.class, statements.get(2)); // module mod1
        assertInstanceOf(Expr.Module.class, ((Stmt.Expression) statements.get(2)).expr);
    }
}
