import java.util.List;

class ConstantFolder implements ASTVisitor<Object>
{
    boolean isTruthy(Object o)
    {
        if (o instanceof Integer)
        {
            if ((Integer) o == 0)
                return false;
            else
                return true;
        }
        else if (o instanceof Number)
        {
            if (((Number)o).doubleValue() == 0)
                return false;
            else
                return true;
        }
        else
            return false;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr)
    {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) // check if module or const
    // this might be tricky, check function evaluation
    {
        if (Carmine.constEnvironment.contains(expr.name))
        {
            return (Carmine.constEnvironment).get(expr.name);
        }

        return null; // it's a module, so leave it as it is
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr)
    {
        Object right = expr.right.fold(this);

        Token operator = expr.operator;

        if (right instanceof Integer)
        {
            Logger.log(expr, "Right is of type Integer", LogLevel.DEBUG);
            if (operator.type == TokenType.MINUS)
                return -(Integer)right;
            else
                Logger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Number)
        {
            Logger.log(expr, "Right is of type Number", LogLevel.DEBUG);
            if (operator.type == TokenType.MINUS)
                return -((Number) right).doubleValue();
            else
                Logger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Boolean)
        {
            Logger.log(expr, "Right is of type Boolean", LogLevel.DEBUG);
            if (operator.type == TokenType.NOT)
                return !((Boolean) right);
            else
                Logger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }

        if (right != null) {
            Logger.log(expr, "Can't apply unary operator to non-const value.", LogLevel.ERROR);
            return null;
        }
        Logger.log(expr, "Can't apply unary operator to non-const value.", LogLevel.WARN); // most likely an identifier

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr)
    {
        Object obj1 = expr.left.fold(this);
        Object obj2 = expr.right.fold(this);

        if (obj1 == null)
        {
            if (obj2 != null)
            {
                expr.right = new Expr.Literal(expr.getLine(), obj2);
                return null;
            }

            Logger.log(expr, "obj1 and obj2 are null", LogLevel.DEBUG);
            Logger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.ERROR);
            return null;
        }

        if (obj2 == null)
        {
            expr.left = new Expr.Literal(expr.getLine(), obj1); // obj1 is not null otherwise the function would have returned null

            Logger.log(expr, "Obj2 is null", LogLevel.DEBUG);
            Logger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.ERROR);
            return null;
        }

        Token operator = expr.operator;

        if (obj1 instanceof Integer && obj2 instanceof Integer)
        {
            Logger.log(expr, "obj1 and obj2 are of type Integer", LogLevel.DEBUG);

            Integer left = (Integer) obj1;
            Integer right = (Integer) obj2;

            switch (operator.type) {
                case PLUS:
                    return left + right;
                case MINUS:
                    return left - right;
                case MUL:
                    return left * right;
                case DIV:
                    return left / right;
                case MOD:
                    return left % right;
                case EXP:
                    return Math.pow(left, right);
                case EQUAL:
                    return left.equals(right);
                case NOTEQUAL:
                    return !left.equals(right);
                case GREATER:
                    return left > right;
                case LESS:
                    return left < right;
                case GREATER_EQUAL:
                    return left >= right;
                case LESS_EQUAL:
                    return left <= right;
                default:
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Number && obj2 instanceof Number) {
            Logger.log(expr, "obj1 and obj2 are of type Number", LogLevel.DEBUG);

            Number left = (Number) obj1;
            Number right = (Number) obj2;

            switch (operator.type) {
                case PLUS:
                    return left.doubleValue() + right.doubleValue();
                case MINUS:
                    return left.doubleValue() - right.doubleValue();
                case MUL:
                    return left.doubleValue() * right.doubleValue();
                case DIV:
                    return left.doubleValue() / right.doubleValue();
                case MOD:
                    return left.doubleValue() % right.doubleValue();
                case EXP:
                    return Math.pow(left.doubleValue(), right.doubleValue());
                case EQUAL:
                    return left.equals(right);
                case NOTEQUAL:
                    return !left.equals(right);
                case GREATER:
                    return left.doubleValue() > right.doubleValue();
                case LESS:
                    return left.doubleValue() < right.doubleValue();
                case GREATER_EQUAL:
                    return left.doubleValue() >= right.doubleValue();
                case LESS_EQUAL:
                    return left.doubleValue() <= right.doubleValue();
                default:
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Boolean && obj2 instanceof Boolean)
        {
            Boolean left = (Boolean)obj1;
            Boolean right = (Boolean)obj2;
            switch (operator.type)
            {
                case EQUAL:
                    return left.equals(right);
                case NOTEQUAL:
                    return !left.equals(right);
                case OR:
                    return left || right;
                case AND:
                    return left && right;
                default:
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Boolean || obj2 instanceof Boolean)
        {
            switch (operator.type) {
                case EQUAL:
                    return isTruthy(obj1) == isTruthy(obj2);
                case NOTEQUAL:
                    return isTruthy(obj1) != isTruthy(obj2);
                default:
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof String && obj2 instanceof String)
        {
            String left = (String)obj1;
            String right = (String)obj2;
            switch (operator.type)
            {
                case PLUS:
                    return left + right;
                case EQUAL:
                    return left.equals(right);
                case NOTEQUAL:
                    return !left.equals(right);
                default:
                    Logger.log(expr, "Operator can't be used on strings: " + operator, LogLevel.ERROR);
                    return null;

            }
        }
        else if (obj1 instanceof String && obj2 instanceof Double)
        {
            String left = (String)obj1;
            Double right = (Double)obj2;

            if (operator.type == TokenType.PLUS)
                return left + right;
            else
            {
                Logger.log(expr, "Operator can't be used on string and double: " + operator, LogLevel.ERROR);
                return null;
            }
        }
        else if (obj1 instanceof Double && obj2 instanceof String)
        {
            Double left = (Double)obj1;
            String right = (String)obj2;

            if (operator.type == TokenType.PLUS)
                return left + right;
            else
            {
                Logger.log(expr, "Operator can't be used on string and double: " + operator, LogLevel.ERROR);
                return null;
            }
        }
        else
        {
            Logger.log(expr, "obj1 or obj2 are of wrong types", LogLevel.DEBUG);
            Logger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.ERROR);
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
        return group.expr.fold(this);
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment assignment)
    {
        Object right = assignment.right.fold(this);
        if (right != null)
            assignment.right = new Expr.Literal(assignment.getLine(), right);

        if (right instanceof Expr.Literal) // !!will have to check for the exception to this rule later, in loops!!
        {
            Logger.log(assignment, "Can't reassign const value.", LogLevel.ERROR);
            return null;
        }

        return right;
    }

    @Override
    public Object visitForStmt(Stmt.For forStmt)
    {
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While whileStmt)
    {
        whileStmt.condition.fold(this);
        whileStmt.body.fold(this); // this will evaluate all the expressions in the body

        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt)
    {
        ifStmt.condition.fold(this);
        ifStmt.thenStmt.fold(this);
        ifStmt.elseStmt.fold(this);

        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt)
    {
        for (Expr.Assignment assignment : enumStmt.assignments)
        {
            assignment.right = new Expr.Literal(assignment.getLine(), assignment.fold(this)); // test this out
        }

        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        constFunction.statements.fold(this);

        return null;
    }


    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        moduleFunction.statements.fold(this);

        return null;
    }

    @Override
    public Object visitModuleStmt(Stmt.Module module)
    {
        return null;
    }

    @Override
    public Object visitConstStmt(Stmt.Const constStmt)
    {
        Object value = constStmt.expr.fold(this);

        if (value != null)
            constStmt.expr = new Expr.Literal(constStmt.expr.getLine(), value);

        /*
        if (!(right instanceof Expr.Literal))
        {
            Logger.log(right, "Const expression is not evaluable.", LogLevel.ERROR); // TO DO: add way to obtain line number

            return null;
        }
         */

        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block block)
    {
        for (Stmt stmt : block.statements)
            stmt.fold(this);

        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression expression)
    {
        expression.expr.fold(this); // if expr is an arithmetic expression, evaluate it and save it

        return null;
    }
}

class ConstantPropagator implements ASTVisitor<Void>
{
    public Void visitLiteralExpr(Expr.Literal expr)
    {
        return null;
    }

    public Void visitUnaryExpr(Expr.Unary expr)
    {
        return null;
    }

    public Void visitBinaryExpr(Expr.Binary expr)
    {
        return null;
    }

    public Void visitCallExpr(Expr.Call call)
    {
        return null;
    }

    public Void visitGroupExpr(Expr.Group group)
    {
        return null;
    }

    public Void visitAssignmentExpr(Expr.Assignment assignment)
    {
        return null;
    }

    public Void visitVariableExpr(Expr.Variable variable)
    {
        return null;
    }

    public Void visitForStmt(Stmt.For forStmt)
    {
        return null;
    }

    public Void visitWhileStmt(Stmt.While whileStmt)
    {
        return null;
    }

    public Void visitIfStmt(Stmt.If ifStmt)
    {
        return null;
    }

    public Void visitEnumStmt(Stmt.Enum enumStmt)
    {
        return null;
    }

    public Void visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        return null;
    }

    public Void visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        return null;
    }

    public Void visitModuleStmt(Stmt.Module module)
    {
        return null;
    }

    public Void visitConstStmt(Stmt.Const constStmt)
    {
        return null;
    }

    public Void visitBlockStmt(Stmt.Block block)
    {
        return null;
    }

    public Void visitExpressionStmt(Stmt.Expression expression)
    {
        return null;
    }

}

public class Optimizer { // travels the AST graph and evaluates arithmetic expressions
                            // these are: Expr.Binary, Expr.Unary, Expr.Literal, Expr.Group and maybe Expr.Variable and Expr.Call

    final ConstantFolder constantFolder = new ConstantFolder();
    final ConstantPropagator constantPropagator = new ConstantPropagator();
    final List<Stmt> statements;
    Optimizer(List<Stmt> statements) // traverse all the statements and search for the expressions
    {
        this.statements = statements;
    }

    void constantFolding()
    {
        for (Stmt statement : statements)
        {
            statement.fold(constantFolder);
        }
    }

    void constantPropagation()
    {

    }

    void loopUnrolling()
    {

    }

    void deadCodeElimination()
    {

    }
}
