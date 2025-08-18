public interface ASTVisitor<T> {
    T visitLiteralExpr(Expr.Literal expr);
    T visitUnaryExpr(Expr.Unary expr);
    T visitBinaryExpr(Expr.Binary expr);
    T visitCallExpr(Expr.Call call);
    T visitGroupExpr(Expr.Group group);
    T visitAssignmentExpr(Expr.Assignment assignment);
    T visitModuleExpr(Expr.Module module);
    T visitVarExpr(Expr.Variable var);
    T visitIdentifierExpr(Expr.Identifier identifier);
    T visitForStmt(Stmt.For forStmt);
    T visitWhileStmt(Stmt.While whileStmt);
    T visitIfStmt(Stmt.If ifStmt);
    T visitEnumStmt(Stmt.Enum enumStmt);
    T visitConstFunctionStmt(Stmt.VarFunction varFunction);
    T visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction);
    T visitBlockStmt(Stmt.Block block);
    T visitExpressionStmt(Stmt.Expression expression);
}
