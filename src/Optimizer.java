import java.util.List;

public class Optimizer implements ASTVisitor<Object> { // travels the AST graph and evaluates arithmetic expressions
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
            statement.evaluate(this);
        }
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

        return expr; // it's a module, so leave it as it is
    }

    @Override
    public Object visitIdentifierExpr(Expr.Variable expr)
    {
        return expr;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr)
    {
        Object right = expr.right.evaluate(this);

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

        Logger.log(expr, "Can't apply unary operator to non-const value.", LogLevel.ERROR);
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr)
    {
        Object obj1 = expr.left.evaluate(this);
        Object obj2 = expr.right.evaluate(this);

        if (obj1 == null || obj2 == null)
        {
            Logger.log(expr, "obj1 or obj2 are null", LogLevel.DEBUG);
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
                    return left.equals(right); // will need to test this later
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
                    return left.equals(right); // will need to test this later
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
                case OR:
                    return left || right;
                case AND:
                    return left && right;
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
        return group.expr.evaluate(this);
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment assignment)
    {
        Object right = assignment.right.evaluate(this);
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
        Expr condition = (Expr)whileStmt.condition.evaluate(this);

        if (!(condition instanceof Expr.Literal))
        {
            Logger.log(condition, "Condition of while loop is not evaluable.", LogLevel.ERROR); // TO DO: add way to get line number
            return null;
        }

        whileStmt.condition = condition; // change condition to the new evaluated value
        whileStmt.body.evaluate(this); // this will evaluate all the expressions in the body

        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt)
    {
        Expr condition = (Expr)ifStmt.condition.evaluate(this);

        if (!(condition instanceof Expr.Literal))
        {
            Logger.log(condition, "Condition of while loop is not evaluable.", LogLevel.ERROR); // TO DO: add way to get line number
            return null;
        }

        ifStmt.condition = condition; // change condition to the new evaluated value

        ifStmt.thenStmt.evaluate(this);
        ifStmt.elseStmt.evaluate(this);

        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt)
    {
        for (Expr.Assignment assignment : enumStmt.assignments)
        {
            assignment.right = new Expr.Literal(assignment.getLine(), assignment.evaluate(this)); // test this out
        }

        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        constFunction.statements.evaluate(this);

        return null;
    }


    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        moduleFunction.statements.evaluate(this);

        return null;
    }

    @Override
    public Object visitModuleStmt(Stmt.Module module)
    {
        Expr right = (Expr)module.expr.evaluate(this);

        if (right instanceof Expr.Literal)
        {
            Logger.log(right, "Can't assign const value to a module.", LogLevel.ERROR); // TO DO: add way to obtain line number
        }

        return null;
    }

    @Override
    public Object visitConstStmt(Stmt.Const constStmt)
    {
        constStmt.expr = new Expr.Literal(constStmt.expr.getLine(), constStmt.expr.evaluate(this));

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
            stmt.evaluate(this);

        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression expression)
    {
        expression.expr = (Expr) expression.expr.evaluate(this); // if expr is an arithmetic expression, evaluate it and save it

        return null;
    }
}
