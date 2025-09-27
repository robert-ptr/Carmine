package carmine.compiler.passes;

import carmine.compiler.helpers.ASTVisitor;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.helpers.LogLevel;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.structures.*;

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
    public Object visitIdentifierExpr(Expr.Identifier expr) // check if module or const
    // this might be tricky, check function evaluation
    {
        if (Carmine.variableEnvironment.contains(expr.getName()))
        {
            return (Carmine.variableEnvironment).get(expr.getName());
        }

        return null; // it's a module, so leave it as it is
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr)
    {
        Object right = expr.right.accept(this);

        Token operator = expr.operator;

        if (right instanceof Integer)
        {
            CarmineLogger.log(expr, "Right is of type Integer", LogLevel.DEBUG);
            if (operator.getType() == TokenType.MINUS)
                return -(Integer)right;
            else
                CarmineLogger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Number)
        {
            CarmineLogger.log(expr, "Right is of type Number", LogLevel.DEBUG);
            if (operator.getType() == TokenType.MINUS)
                return -((Number) right).doubleValue();
            else
                CarmineLogger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Boolean)
        {
            CarmineLogger.log(expr, "Right is of type Boolean", LogLevel.DEBUG);
            if (operator.getType() == TokenType.NOT)
                return !((Boolean) right);
            else
                CarmineLogger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }

        if (right != null) {
            CarmineLogger.log(expr, "Can't apply unary operator to non-const value.", LogLevel.ERROR);
            return null;
        }
        CarmineLogger.log(expr, "Can't apply unary operator to non-const value.", LogLevel.WARN); // most likely an identifier

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr)
    {
        Object obj1 = expr.left.accept(this);
        Object obj2 = expr.right.accept(this);

        if (obj1 == null)
        {
            if (obj2 != null)
            {
                expr.right = new Expr.Literal(expr.getLine(), obj2);
                return null;
            }

            CarmineLogger.log(expr, "obj1 and obj2 are null", LogLevel.DEBUG);
            CarmineLogger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.WARN);
            return null;
        }

        if (obj2 == null)
        {
            expr.left = new Expr.Literal(expr.getLine(), obj1); // obj1 is not null otherwise the function would have returned null

            CarmineLogger.log(expr, "Obj2 is null", LogLevel.DEBUG);
            CarmineLogger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.WARN);
            return null;
        }

        Token operator = expr.operator;

        if (obj1 instanceof Integer && obj2 instanceof Integer)
        {
            CarmineLogger.log(expr, "obj1 and obj2 are of type Integer", LogLevel.DEBUG);

            Integer left = (Integer) obj1;
            Integer right = (Integer) obj2;

            switch (operator.getType()) {
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
                    CarmineLogger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Number && obj2 instanceof Number) {
            CarmineLogger.log(expr, "obj1 and obj2 are of type Number", LogLevel.DEBUG);

            Number left = (Number) obj1;
            Number right = (Number) obj2;

            switch (operator.getType()) {
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
                    CarmineLogger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Boolean && obj2 instanceof Boolean)
        {
            Boolean left = (Boolean)obj1;
            Boolean right = (Boolean)obj2;
            switch (operator.getType())
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
                    CarmineLogger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Boolean || obj2 instanceof Boolean)
        {
            switch (operator.getType()) {
                case EQUAL:
                    return isTruthy(obj1) == isTruthy(obj2);
                case NOTEQUAL:
                    return isTruthy(obj1) != isTruthy(obj2);
                default:
                    CarmineLogger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof String && obj2 instanceof String)
        {
            String left = (String)obj1;
            String right = (String)obj2;
            switch (operator.getType())
            {
                case PLUS:
                    return left + right;
                case EQUAL:
                    return left.equals(right);
                case NOTEQUAL:
                    return !left.equals(right);
                default:
                    CarmineLogger.log(expr, "Operator can't be used on strings: " + operator, LogLevel.ERROR);
                    return null;

            }
        }
        else if (obj1 instanceof String && obj2 instanceof Double)
        {
            String left = (String)obj1;
            Double right = (Double)obj2;

            if (operator.getType() == TokenType.PLUS)
                return left + right;
            else
            {
                CarmineLogger.log(expr, "Operator can't be used on string and double: " + operator, LogLevel.ERROR);
                return null;
            }
        }
        else if (obj1 instanceof Double && obj2 instanceof String)
        {
            Double left = (Double)obj1;
            String right = (String)obj2;

            if (operator.getType() == TokenType.PLUS)
                return left + right;
            else
            {
                CarmineLogger.log(expr, "Operator can't be used on string and double: " + operator, LogLevel.ERROR);
                return null;
            }
        }
        else
        {
            CarmineLogger.log(expr, "obj1 or obj2 are of wrong types", LogLevel.DEBUG);
            CarmineLogger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.ERROR);
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
        Object right = assignment.right.accept(this);
        if (right != null)
            assignment.right = new Expr.Literal(assignment.getLine(), right);

        return right;
    }

    @Override
    public Object visitModuleExpr(Expr.Module module) {
        return null;
    }

    @Override
    public Object visitVarExpr(Expr.Variable var) {
        var.assignment.accept(this);

        return null;
    }

    @Override
    public Object visitForStmt(Stmt.For forStmt)
    {
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While whileStmt)
    {
        whileStmt.condition.accept(this);
        whileStmt.body.accept(this); // this will evaluate all the expressions in the body

        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt)
    {
        ifStmt.condition.accept(this);
        ifStmt.thenStmt.accept(this);
        ifStmt.elseStmt.accept(this);

        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt)
    {
        for (Expr.Assignment assignment : enumStmt.assignments)
        {
            assignment.right = new Expr.Literal(assignment.getLine(), assignment.accept(this)); // test this out
        }

        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.VarFunction varFunction)
    {
        varFunction.statements.accept(this);

        return null;
    }


    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        moduleFunction.statements.accept(this);

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
        Object obj = expression.expr.accept(this); // if expr is an arithmetic expression, evaluate it and save it
        if (obj instanceof Integer ||
            obj instanceof Number ||
            obj instanceof String)
            expression.expr = new Expr.Literal(expression.expr.getLine(),obj);

        return null;
    }
}

class ConstantPropagator implements ASTVisitor<Object> {
    Environment varEnvironment = new Environment();
    Environment moduleEnvironment = new Environment();

    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr;
    }

    public Object visitUnaryExpr(Expr.Unary expr) {
        if (expr.right instanceof Expr.Variable) {
            expr.right = (Expr.Literal)expr.right.accept(this);
        }

        return null;
    }

    public Object visitBinaryExpr(Expr.Binary expr) {
        if (expr.left instanceof Expr.Variable)
            expr.left = (Expr.Literal)expr.left.accept(this);

        if (expr.right instanceof Expr.Variable)
            expr.right = (Expr.Literal)expr.right.accept(this);

        return null;
    }

    public Object visitCallExpr(Expr.Call call) {
        return null;
    } // TO DO

    public Object visitGroupExpr(Expr.Group group) {
        group.expr.accept(this);

        return null;
    }

    public Object visitAssignmentExpr(Expr.Assignment assignment)
    {
        assignment.right.accept(this);

        Environment moduleEnv = moduleEnvironment;
        while (moduleEnv != null && !moduleEnv.contains(assignment.name)) {
            moduleEnv = (Environment) moduleEnv.getEnclosing();
        }

        if (moduleEnv != null)
        {
            if (assignment.right instanceof Expr.Literal)
                moduleEnv.getVariables().put(assignment.name.getLexeme(), assignment.right);

            return null;
        }

        Environment variableEnv = varEnvironment;
        while (variableEnv != null && !variableEnv.contains(assignment.name)) {
            variableEnv = (Environment) variableEnv.getEnclosing();
        }

        if (variableEnv != null)
        {
            if (assignment.right instanceof Expr.Literal)
                variableEnv.getVariables().put(assignment.name.getLexeme(), assignment.right);

            return null;
        }

        return null;
    }

    @Override
    public Object visitIdentifierExpr(Expr.Identifier identifier) { // TO DO: search the other environments
        Environment moduleEnv = moduleEnvironment;
        Environment variableEnv = varEnvironment;

        if (varEnvironment.contains(identifier.name))
            return varEnvironment.get(identifier.name);
        else if ((moduleEnvironment.contains(identifier.name)))
            return moduleEnvironment.get(identifier.name);

        return null;
    }

    public Object visitForStmt(Stmt.For forStmt) {
        return null;
    }

    public Object visitWhileStmt(Stmt.While whileStmt) {
        return null;
    }

    public Object visitIfStmt(Stmt.If ifStmt) {
        return null;
    }

    public Object visitEnumStmt(Stmt.Enum enumStmt) {
        return null;
    }

    public Object visitConstFunctionStmt(Stmt.VarFunction varFunction) {
        return null;
    }

    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction) {
        return null;
    }

    public Object visitModuleExpr(Expr.Module module) {
        Environment env = moduleEnvironment;
        while (env != null && !env.contains(module.getName())) {
            env = (Environment) env.getEnclosing();
        }

        if (env != null)
            throw new RuntimeException("Module " + module.getName() + " is already defined.");

        Carmine.moduleEnvironment.put(module.getName().getLexeme(), module.assignment.accept(this));

        return null;
    }

    public Object visitVarExpr(Expr.Variable var) {
        Environment env = varEnvironment;
        while (env != null && !env.contains(var.getName())) {
            env = (Environment) env.getEnclosing();
        }

        if (env != null)
            throw new RuntimeException("Variable " + var.getName() + " is already defined.");
        varEnvironment.put(var.getName().getLexeme(), var.assignment.accept(this));

        return null;
    }

    public Object visitBlockStmt(Stmt.Block block) {
        Environment blockConstEnvironment = new Environment();
        blockConstEnvironment.addEnclosing(varEnvironment);

        varEnvironment = blockConstEnvironment;

        for (Stmt stmt : block.statements) {
            stmt.accept(this);
        }

        varEnvironment = blockConstEnvironment.getEnclosing();

        return null;
    }

    public Object visitExpressionStmt(Stmt.Expression expression) {
        return null;
    }

}

class SSAConverter implements ASTVisitor<Object>
{

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call call) {
        return null;
    }

    @Override
    public Object visitGroupExpr(Expr.Group group) {
        return null;
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment assignment) {
        return null;
    }

    @Override
    public Object visitModuleExpr(Expr.Module module) {
        return null;
    }

    @Override
    public Object visitVarExpr(Expr.Variable var) {
        return null;
    }

    @Override
    public Object visitIdentifierExpr(Expr.Identifier identifier) {
        return null;
    }

    @Override
    public Object visitForStmt(Stmt.For forStmt) {
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While whileStmt) {
        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt) {
        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt) {
        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.VarFunction varFunction) {
        return null;
    }

    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction) {
        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block block) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression expression) {
        return null;
    }
}

class LoopUnroller implements ASTVisitor<Object>
{
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call call) {
        return null;
    }

    @Override
    public Object visitGroupExpr(Expr.Group group) {
        return null;
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment assignment) {
        return null;
    }

    @Override
    public Object visitModuleExpr(Expr.Module module) {
        return null;
    }

    @Override
    public Object visitVarExpr(Expr.Variable var) {
        return null;
    }

    @Override
    public Object visitIdentifierExpr(Expr.Identifier identifier) {
        return null;
    }

    @Override
    public Object visitForStmt(Stmt.For forStmt) {
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While whileStmt) {
        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If ifStmt) {
        return null;
    }

    @Override
    public Object visitEnumStmt(Stmt.Enum enumStmt) {
        return null;
    }

    @Override
    public Object visitConstFunctionStmt(Stmt.VarFunction varFunction) {
        return null;
    }

    @Override
    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction) {
        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block block) {
        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression expression) {
        return null;
    }
}

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
