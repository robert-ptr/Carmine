import java.util.List;

public class Optimizer implements ConstVisitor<Object> { // travels the AST graph and evaluates arithmetic expressions
                            // these are: Expr.Binary, Expr.Unary, Expr.Literal, Expr.Group and maybe Expr.Variable and Expr.Call
    final List<Stmt> statements;
    Optimizer(List<Stmt> statements) // traverse all the statements and search for the expressions
    {
        this.statements = statements;
    }

    void evaluateArithmeticAST()
    {
        for (Stmt statement : statements)
        {
            statement.accept(this);
        }
    }
    @Override
    public Object visitLiteralExpr(Expr.Literal expr)
    {
        return expr;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) // check if module or const
                                                        // this might be tricky, check function evaluation
    {
        if (Carmine.constEnvironment.contains(expr.name))
        {
            return new Expr.Literal(Carmine.constEnvironment.get(expr.name));
        }

        return expr; // it's a module, so leave it as it is
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr)
    {
        Expr right = (Expr) expr.right.accept(this);
        if (right instanceof Expr.Literal)
        {
            return new Expr.Literal(expr.evaluate());
        }
        else
        {
            Carmine.error(expr.operator.line + "Can't apply unary operator to non-const value.");
            return null;
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr)
    {
        Expr left = (Expr) expr.left.accept(this);
        Expr right = (Expr) expr.right.accept(this);

        if (left instanceof Expr.Literal && right instanceof Expr.Literal)
        {
            return new Expr.Literal(expr.evaluate());
        }
        else
        {
            Carmine.error(expr.operator.line + "Can't apply binary operator to non-const value.");
            return null;
        }
    }

    @Override
    public Object visitCallExpr(Expr.Call call) // TO DO
    {
        return null;
    }

    @Override
    public Object visitGroupExpr(Expr.Group group)
    {
        return group.expr.accept(this);
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment assignment)
    {
        Expr right = (Expr) assignment.right.accept(this);

        if (right instanceof Expr.Literal) // !!will have to check for exception later, in loops!!
        {
            Carmine.error(assignment.name.line + "Can't reassign const value.");
            return null;
        }

        return assignment;
    }

    @Override
    public Object visitForStmt(Stmt.For forStmt)
    {
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While whileStmt)
    {
        Expr condition = (Expr)whileStmt.condition.accept(this);

        if (!(condition instanceof Expr.Literal))
        {
            Carmine.error("Condition of while loop is not evaluable."); // TO DO: add way to get line number
            return null;
        }

        whileStmt.condition = condition; // change condition to the new evaluated value
        whileStmt.body.accept(this); // this will evaluate all the expressions in the body

        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt)
    {
        Expr condition = (Expr)ifStmt.condition.accept(this);

        if (!(condition instanceof Expr.Literal))
        {
            Carmine.error("Condition of while loop is not evaluable."); // TO DO: add way to get line number
            return null;
        }

        ifStmt.condition = condition; // change condition to the new evaluated value

        ifStmt.thenStmt.accept(this);
        ifStmt.elseStmt.accept(this);

        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt)
    {
        for (Expr assignment : enumStmt.assignments)
        {
            assignment = (Expr.Assignment)assignment.accept(this); // test this out
        }

        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        constFunction.statements.accept(this);

        return null;
    }


    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        moduleFunction.statements.accept(this);

        return null;
    }

    @Override
    public Object visitModuleStmt(Stmt.Module module)
    {
        Expr right = (Expr)module.expr.accept(this);

        if (right instanceof Expr.Literal)
        {
            Carmine.error("Can't assign const value to a module."); // TO DO: add way to obtain line number
        }

        return null;
    }

    @Override
    public Object visitConstStmt(Stmt.Const constStmt)
    {
        Expr right = (Expr)constStmt.expr.accept(this);

        if (!(right instanceof Expr.Literal))
        {
            Carmine.error("Const expression is not evaluable."); // TO DO: add way to obtain line number

            return null;
        }

        constStmt.expr = right;

        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block block)
    {
        for (Stmt stmt : block.statements)
            stmt.accept(this);

        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression expression)
    {
        expression.expr = (Expr) expression.expr.accept(this); // if expr is an arithmetic expression, evaluate it and save it

        return null;
    }
}
