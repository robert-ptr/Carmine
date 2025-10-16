package carmine.compiler.passes;

import carmine.compiler.helpers.ASTVisitor;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.helpers.LogLevel;
import carmine.compiler.structures.*;

import java.util.List;

public class Optimizer { // travels the AST graph and evaluates arithmetic expressions
                            // these are: Expr.Binary, Expr.Unary, Expr.Literal, Expr.Group and maybe Expr.Variable and Expr.Call

    final ConstantFolder constantFolder = new ConstantFolder();
    final ConstantPropagator constantPropagator = new ConstantPropagator();
    final SSAConverter ssaConverter = new SSAConverter();
    final LoopUnroller loopUnroller = new LoopUnroller();
    final List<Stmt> statements;

    Optimizer(List<Stmt> statements) // traverse all the statements and search for the expressions
    {
        this.statements = statements;
    }

    void constantFolding()
    {
        for (Stmt statement : statements)
        {
            statement.accept(constantFolder);
        }
    }

    void constantPropagation()
    {
        constantPropagator.varEnvironment = new Environment();
        for (Stmt statement : statements)
        {
            statement.accept(constantPropagator);
        }
    }

    void convertToSSA()
    {
        for (Stmt statement : statements)
        {
            statement.accept(ssaConverter);
        }
    }

    void loopUnrolling()
    {
        for (Stmt statement : statements)
        {
            statement.accept(loopUnroller);
        }
    }

    void deadCodeElimination()
    {

    }
}
