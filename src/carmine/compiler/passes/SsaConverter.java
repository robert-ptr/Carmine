package carmine.compiler.passes;

import carmine.compiler.helpers.AstVisitor;
import carmine.compiler.structures.*;

class SsaConverter implements AstVisitor<Object>
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