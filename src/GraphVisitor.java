public interface GraphVisitor<T> {
    T visitLiteralExpr(Expr.Literal expr);
    T visitIdentifierExpr(Expr.Variable expr);
    T visitUnaryExpr(Expr.Unary expr);
    T visitBinaryExpr(Expr.Binary expr);
    T visitCallExpr(Expr.Call call);
    T visitGroupExpr(Expr.Group group);
    T visitAssignmentExpr(Expr.Assignment assignment);
    T visitVariableExpr(Expr.Variable variable);
    T visitForStmt(Stmt.For forStmt);
    T visitWhileStmt(Stmt.While whileStmt);
    T visitIfStmt(Stmt.If ifStmt);
    T visitEnumStmt(Stmt.Enum enumStmt);
    T visitConstFunctionStmt(Stmt.ConstFunction constFunction);
    T visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction);
    T visitModuleStmt(Stmt.Module module);
    T visitConstStmt(Stmt.Const constStmt);
    T visitBlockStmt(Stmt.Block block);
    T visitExpressionStmt(Stmt.Expression expression);
}
