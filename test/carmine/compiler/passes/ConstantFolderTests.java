package carmine.compiler.passes;

import carmine.compiler.structures.Expr;
import carmine.compiler.structures.Stmt;
import carmine.compiler.structures.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ConstantFolderTests {
    @Test
    void testArithmeticOptimization() {
        String source = "(3 + 7 - (3 * 2)) / 4;";

        Scanner scanner = new Scanner(source);
        ArrayList<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        Optimizer optimizer = new Optimizer(statements);

        optimizer.constantFolding();

        assert (statements.size() == 1);
        assert (statements.get(0) instanceof Stmt.Expression);
        Expr expr = ((Stmt.Expression) statements.get(0)).expr;
        assert (expr instanceof Expr.Literal);
        assert (((Expr.Literal) expr).value.equals(1));
    }

}
