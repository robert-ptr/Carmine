package carmine.compiler.passes;

import carmine.compiler.helpers.ASTVisitor;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.helpers.LogLevel;
import carmine.compiler.structures.*;

import java.util.List;

class ConstantPropagator implements ASTVisitor<Object> {
    Environment varEnvironment = new Environment();
    Environment moduleEnvironment = new Environment();

    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = expr.right.accept(this);

        if (right != null)
            expr.right = new Expr.Literal(expr.getLine(), right);

        return null;
    }

    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        if (left != null)
            expr.left = new Expr.Literal(expr.getLine(), left);
        if (right != null)
            expr.right = new Expr.Literal(expr.getLine(), right);

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

        return null;
    }

    @Override
    public Object visitIdentifierExpr(Expr.Identifier identifier) {
        Environment varEnv = varEnvironment;

        while (varEnv != null) {
            if (varEnvironment.contains(identifier.name))
                return ((Expr.Literal) varEnvironment.get(identifier.name)).value;

            varEnv = varEnv.getEnclosing();
        }

        return null;
    }

    public Object visitForStmt(Stmt.For forStmt) {
        forStmt.minValue.accept(this);
        forStmt.maxValue.accept(this);
        forStmt.body.accept(this);

        return null;
    }

    public Object visitWhileStmt(Stmt.While whileStmt) {
        whileStmt.condition.accept(this);
        whileStmt.body.accept(this);

        return null;
    }

    public Object visitIfStmt(Stmt.If ifStmt) {
        ifStmt.condition.accept(this);
        ifStmt.thenStmt.accept(this);

        if (ifStmt.elseStmt != null)
            ifStmt.elseStmt.accept(this);

        return null;
    }

    public Object visitEnumStmt(Stmt.Enum enumStmt) {
        Environment newEnv = new Environment();
        newEnv.addEnclosing(varEnvironment);
        varEnvironment = newEnv;

        for (var expr : enumStmt.assignments) {
            expr.accept(this);
            if (expr.right instanceof Expr.Literal)
                varEnvironment.getVariables().put(expr.name.getLexeme(), expr.right);

        }

        varEnvironment = varEnvironment.getEnclosing();
        return null;
    }

    public Object visitConstFunctionStmt(Stmt.VarFunction varFunction) {
        Environment newEnv = new Environment();
        newEnv.addEnclosing(varEnvironment);
        varEnvironment = newEnv;

        varFunction.statements.accept(this);

        varEnvironment = varEnvironment.getEnclosing();
        return null;
    }

    public Object visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction) {
        Environment newEnv = new Environment();
        newEnv.addEnclosing(varEnvironment);
        varEnvironment = newEnv;

        moduleFunction.statements.accept(this);

        varEnvironment = varEnvironment.getEnclosing();
        return null;
    }

    public Object visitModuleExpr(Expr.Module module) {
        return null;
    }

    public Object visitVarExpr(Expr.Variable var) {
        Environment env = varEnvironment;
        while (env != null && !env.contains(var.getName())) {
            env = (Environment) env.getEnclosing();
        }

        if (env != null)
            throw new RuntimeException("Variable " + var.getName() + " is already defined.");

        var.assignment.accept(this);

        if (var.assignment.right instanceof Expr.Literal)
            varEnvironment.put(var.getName().getLexeme(), var.assignment.right);

        return null;
    }

    public Object visitBlockStmt(Stmt.Block block) {
        Environment newEnv = new Environment();
        newEnv.addEnclosing(varEnvironment);
        varEnvironment = newEnv;

        for (Stmt stmt : block.statements) {
            stmt.accept(this);
        }

        varEnvironment = varEnvironment.getEnclosing();

        return null;
    }

    public Object visitExpressionStmt(Stmt.Expression expression) {
        expression.expr.accept(this);

        return null;
    }

}