package carmine.compiler.passes

import carmine.compiler.structures.Expr
import carmine.compiler.structures.Stmt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

// for now this only tests that parsing finishes without errors
// more complicated cases should be tested in the future, such as:
// running code with syntax errors and checking the errors
// going through the generated AST and ensuring it is as expected
class ParserTests {
    private fun parse(source: String): List<Stmt?> {
        val tokens = Scanner(source).scanTokens()
        return Parser(tokens).parse()
    }

    // asserts the statement is an expression statement wrapping an Expr of type E,
    // and returns that inner expression
    private inline fun <reified E : Expr> assertExpressionOf(stmt: Stmt?): E {
        val expression = assertInstanceOf(Stmt.Expression::class.java, stmt)
        return assertInstanceOf(E::class.java, expression.expr)
    }

    @Test
    fun testModulesAndLogic() {
        val source = """
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
            """.trimIndent()

        val statements = parse(source)

        assertEquals(5, statements.size)

        val xor = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[0])
        assertEquals("XOR", xor.name.lexeme)

        val nand = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[1])
        assertEquals("NAND", nand.name.lexeme)

        assertEquals("operand1", assertExpressionOf<Expr.Module>(statements[2]).assignment.name.lexeme)
        assertEquals("operand2", assertExpressionOf<Expr.Module>(statements[3]).assignment.name.lexeme)
        assertEquals("output", assertExpressionOf<Expr.Module>(statements[4]).assignment.name.lexeme)
    }

    @Test
    fun testEmptyProgram() {
        val statements = parse("")

        // 0 statements expected
        assertEquals(0, statements.size)
    }

    @Test
    fun testBlockWithVariable() {
        val source = """
            {
                var test = 0;
            }
            """.trimIndent()

        val statements = parse(source)

        // 1 block statement expected
        assertEquals(1, statements.size)

        val block = assertInstanceOf(Stmt.Block::class.java, statements[0])
        assertEquals(1, block.statements.size)
        // var test = 0; inside block is parsed as Expression(Variable)
        assertExpressionOf<Expr.Variable>(block.statements[0])
    }

    @Test
    fun testSimpleComment() {
        val statements = parse("// hello")

        assertEquals(0, statements.size)
    }

    @Test
    fun testArithmeticExpression() {
        val statements = parse("(3 + 7 - (3 * 2)) / 4;")

        assertEquals(1, statements.size)
        assertExpressionOf<Expr.Binary>(statements[0])
    }

    @Test
    fun testEnumAndVariables() {
        val source = """
            enum
            {
                MIN = 5 + 3,
                MAX = MIN + 10
            };

            3+7;

            var b = 7;
            var a = (-5 + 3 * (-2)) + b;
            """.trimIndent()

        val statements = parse(source)

        assertEquals(4, statements.size)
        assertInstanceOf(Stmt.Enum::class.java, statements[0])
        assertInstanceOf(Stmt.Expression::class.java, statements[1]) // 3+7

        assertExpressionOf<Expr.Variable>(statements[2]) // var b = 7
        assertExpressionOf<Expr.Variable>(statements[3]) // var a = ...
    }

    @Test
    fun testVariablesAndModules() {
        val source = """
            var b = -3 + 7 * 8 / (6 + 2);
            var a = b * 6 - c;
            var c = 78;

            module INV() -> out
            {
            }

            module AND3() -> out
            {
            }
            """.trimIndent()

        val statements = parse(source)

        // 5 statements expected
        assertEquals(5, statements.size)

        assertExpressionOf<Expr.Variable>(statements[0]) // var b
        assertExpressionOf<Expr.Variable>(statements[1]) // var a
        assertExpressionOf<Expr.Variable>(statements[2]) // var c

        val inv = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[3])
        assertEquals("INV", inv.name.lexeme)

        val and3 = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[4])
        assertEquals("AND3", and3.name.lexeme)
    }

    @Test
    fun testEmptyModules() {
        val source = """
            module INV() -> out
            {
            }

            module AND3() -> out
            {
            }
            """.trimIndent()

        val statements = parse(source)

        assertEquals(2, statements.size)

        val inv = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[0])
        assertEquals("INV", inv.name.lexeme)

        val and3 = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[1])
        assertEquals("AND3", and3.name.lexeme)
    }

    @Test
    fun testComplexModulesWithLoops() {
        val source = """
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
            """.trimIndent()

        val statements = parse(source)

        assertEquals(5, statements.size)

        assertInstanceOf(Stmt.Enum::class.java, statements[0])

        assertExpressionOf<Expr.Variable>(statements[1]) // var val

        val xor10 = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[2])
        assertEquals("xor10", xor10.name.lexeme)
        // check while loop inside xor10
        // xor10 statements: var i = 0; while...
        assertEquals(2, xor10.statements.statements.size)
        assertInstanceOf(Stmt.While::class.java, xor10.statements.statements[1])

        val xorc = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[3])
        assertEquals("xorc", xorc.name.lexeme)
        // check if inside xorc
        assertEquals(1, xorc.statements.statements.size)
        assertInstanceOf(Stmt.If::class.java, xorc.statements.statements[0])

        val xor1 = assertInstanceOf(Stmt.ModuleFunction::class.java, statements[4])
        assertEquals("xor1", xor1.name.lexeme)
    }

    @Test
    fun testEnumIntensity() {
        val source = """
            enum INTENSITY
            {
                MAX = 10,
                MIN = 2,
                MEDIUM = 5,
            };

            var val = null;
            module mod1 = null;
            """.trimIndent()

        val statements = parse(source)

        assertEquals(3, statements.size)

        assertInstanceOf(Stmt.Enum::class.java, statements[0])

        assertExpressionOf<Expr.Variable>(statements[1]) // var val
        assertExpressionOf<Expr.Module>(statements[2]) // module mod1
    }
}
