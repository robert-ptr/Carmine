package carmine.compiler.passes;

import carmine.compiler.structures.Expr;
import carmine.compiler.structures.Stmt;
import carmine.compiler.structures.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ConstantFolderTests {
    @Test
    void test0()
    {

    }

    @Test
    void test1()
    {

    }

    @Test
    void test2()
    {

    }

    @Test
    void test3()
    {

    }

    @Test
    void test4() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("./test/test4.txt"));

        Scanner scanner = new Scanner(new String(bytes, Charset.defaultCharset()));
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        Optimizer optimizer = new Optimizer(statements);

        optimizer.constantFolding();

        assert(statements.size() == 1);
        assert(statements.get(0) instanceof Stmt.Expression);
        Expr expr = ((Stmt.Expression)statements.get(0)).expr;
        assert(expr instanceof Expr.Literal);
        assert(((Expr.Literal)expr).value.equals(1));
    }

    @Test
    void test5()
    {

    }

    @Test
    void test6()
    {

    }

    @Test
    void test7()
    {

    }

    @Test
    void test8()
    {

    }

    @Test
    void test9()
    {

    }
}
