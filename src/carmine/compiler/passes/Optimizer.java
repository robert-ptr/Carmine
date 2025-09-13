package carmine.compiler.passes;

import carmine.compiler.helpers.ASTVisitor;
import carmine.compiler.helpers.LogLevel;
import carmine.compiler.helpers.Logger;
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
            Logger.log(expr, "Right is of type Integer", LogLevel.DEBUG);
            if (operator.getType() == TokenType.MINUS)
                return -(Integer)right;
            else
                Logger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Number)
        {
            Logger.log(expr, "Right is of type Number", LogLevel.DEBUG);
            if (operator.getType() == TokenType.MINUS)
                return -((Number) right).doubleValue();
            else
                Logger.log(expr, "Can't apply operator " + operator + " to " + right, LogLevel.ERROR);
        }
        else if (right instanceof Boolean)
        {
            Logger.log(expr, "Right is of type Boolean", LogLevel.DEBUG);
            if (operator.getType() == TokenType.NOT)
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
        Object obj1 = expr.left.accept(this);
        Object obj2 = expr.right.accept(this);

        if (obj1 == null)
        {
            if (obj2 != null)
            {
                expr.right = new Expr.Literal(expr.getLine(), obj2);
                return null;
            }

            Logger.log(expr, "obj1 and obj2 are null", LogLevel.DEBUG);
            Logger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.WARN);
            return null;
        }

        if (obj2 == null)
        {
            expr.left = new Expr.Literal(expr.getLine(), obj1); // obj1 is not null otherwise the function would have returned null

            Logger.log(expr, "Obj2 is null", LogLevel.DEBUG);
            Logger.log(expr, "Can't apply binary operator to non-const value.", LogLevel.WARN);
            return null;
        }

        Token operator = expr.operator;

        if (obj1 instanceof Integer && obj2 instanceof Integer)
        {
            Logger.log(expr, "obj1 and obj2 are of type Integer", LogLevel.DEBUG);

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
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
                    return null;
            }
        }
        else if (obj1 instanceof Number && obj2 instanceof Number) {
            Logger.log(expr, "obj1 and obj2 are of type Number", LogLevel.DEBUG);

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
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
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
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
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
                    Logger.log(expr, "Unknown binary operator: " + operator, LogLevel.ERROR);
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
                    Logger.log(expr, "Operator can't be used on strings: " + operator, LogLevel.ERROR);
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
                Logger.log(expr, "Operator can't be used on string and double: " + operator, LogLevel.ERROR);
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
        expression.expr.accept(this); // if expr is an arithmetic expression, evaluate it and save it

        return null;
    }
}

class ConstantPropagator implements ASTVisitor<Object> {
    public Object visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    public Object visitUnaryExpr(Expr.Unary expr) {
        if (expr.right instanceof Expr.Variable) {
            expr.right = new Expr.Literal(expr.right.getLine(), expr.right.accept(this));
        }

        return null;
    }

    public Object visitBinaryExpr(Expr.Binary expr) {
        if (expr.left instanceof Expr.Variable)
            expr.left = new Expr.Literal(expr.left.getLine(), expr.left.accept(this));

        if (expr.right instanceof Expr.Variable)
            expr.right = new Expr.Literal(expr.right.getLine(), expr.left.accept(this));

        return null;
    }

    public Object visitCallExpr(Expr.Call call) {
        return null;
    } // TO DO

    public Object visitGroupExpr(Expr.Group group) {
        group.expr.accept(this);

        return null;
    }

    public Object visitAssignmentExpr(Expr.Assignment assignment) {
        ModuleEnvironment env = Carmine.moduleEnvironment;
        Object value = assignment.right.accept(this);

        if (value != null)
            env.put(assignment.name.getLexeme(), value);

        return value;
    }

    @Override
    public Object visitIdentifierExpr(Expr.Identifier identifier) { // TO DO: search the other environments
        ModuleEnvironment moduleEnv = Carmine.moduleEnvironment;
        VariableEnvironment variableEnv = Carmine.variableEnvironment;


        if (Carmine.variableEnvironment.contains(identifier.name))
            return Carmine.variableEnvironment.get(identifier.name);
        else if ((Carmine.moduleEnvironment.contains(identifier.name)))
            return Carmine.moduleEnvironment.get(identifier.name);

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
        ModuleEnvironment env = Carmine.moduleEnvironment;
        while (env != null && !env.contains(module.getName())) {
            env = (ModuleEnvironment) env.getEnclosing();
        }

        if (env != null)
            throw new RuntimeException("Module " + module.getName() + " is already defined.");

        Carmine.variableEnvironment.put(module.getName().getLexeme(), module.assignment.accept(this));

        return null;
    }

    public Object visitVarExpr(Expr.Variable var) {
        Environment env = Carmine.variableEnvironment;
        while (env != null && !env.contains(var.getName())) {
            env = (Environment) env.getEnclosing();
        }

        if (env != null)
            throw new RuntimeException("Variable " + var.getName() + " is already defined.");

        Carmine.variableEnvironment.put(var.getName().getLexeme(), var.assignment.accept(this));

        return null;
    }

    public Object visitBlockStmt(Stmt.Block block) {
        VariableEnvironment blockConstEnvironment = new VariableEnvironment();
        blockConstEnvironment.addEnclosing(Carmine.variableEnvironment);

        Carmine.variableEnvironment = blockConstEnvironment;

        for (Stmt stmt : block.statements) {
            stmt.accept(this);
        }

        Carmine.variableEnvironment = blockConstEnvironment.getEnclosing();

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
