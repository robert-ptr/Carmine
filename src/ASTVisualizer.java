public class ASTVisualizer { // used for debugging
    private int nodeId = 0;
    private StringBuilder builder = new StringBuilder();

    public String generateDot(Expr expr)
    {
        builder.append("digraph AST {\n");
        expr.dotWalk(builder, -1);
        builder.append("}\n");
        return builder.toString();
    }

    public String generateDot(Stmt stmt)
    {
        builder.append("digraph AST {\n");
        expr.walk(stmt, -1);
        builder.append("}\n");
        return builder.toString();
    }
}
