import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeVisualizer implements ASTVisitor<Void> {
    public static List<ConstEnvironment> environments = new ArrayList<ConstEnvironment>();

    private int nodeId = 0;
    private StringBuilder builder = new StringBuilder();

    public static void printEnvironments() {
        environments.add(Carmine.constEnvironment);

        for (ConstEnvironment constEnvironment : environments) {
            System.out.println("environment");
            HashMap<String, Object> variables = constEnvironment.getVariables();

            for (String key : variables.keySet()) {
                System.out.println(key + ": " + variables.get(key));
            }
        }
    }

    public String visualizeAST(List<Stmt> statements) {
        builder.append("digraph AST {\n");

        for (Stmt stmt : statements)
        {
            stmt.buildTree(this);
        }

        builder.append("}\n");
        return builder.toString();
    }

    public void createNode(Expr expr)
    {
        createNode(expr.toString());
    }

    public void createNode(Token expr)
    {
        createNode(expr.toString());
    }

    public void createNode(String expr)
    {
        builder.append("node" + nodeId++ + "[label=\"" + expr + "\"];\n");
    }

    public void createNode(Object obj) { builder.append("node" + nodeId++ + "[label=\"" + obj + "\"];\n"); }

    public void createConnection(int id1, int id2)
    {
        builder.append("node" + id1 + " -> " + "node" + id2 + ";\n");
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        createNode(expr.value);

        return null;
    }

    @Override
    public Void visitIdentifierExpr(Expr.Variable expr) {
        createNode(expr.name);

        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        createNode(expr.operator);
        createConnection(nodeId - 1, nodeId);
        expr.right.buildTree(this);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        int initialId = nodeId;
        createNode(expr.operator);

        createConnection(initialId, nodeId);

        expr.left.buildTree(this);
        createConnection(initialId, nodeId);
        expr.right.buildTree(this);

        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call call) {
        int initialId = nodeId;

        createNode(call.callee);

        for (Expr argument : call.arguments)
        {
            createConnection(initialId, nodeId);
            createNode(argument);
        }

        return null;
    }

    @Override
    public Void visitGroupExpr(Expr.Group group) {
        createNode("GROUP");
        createConnection(nodeId - 1, nodeId);
        group.expr.buildTree(this);

        return null;
    }

    @Override
    public Void visitAssignmentExpr(Expr.Assignment assignment) {
        createNode("ASSIGNMENT " + assignment.name);
        createConnection(nodeId - 1, nodeId);
        assignment.right.buildTree(this);

        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable variable) {
        createNode(variable.name);

        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For forStmt) { // TO DO
        int initialId = nodeId;

        createNode("FOR");
        createConnection(initialId, nodeId);
        forStmt.body.buildTree(this);

        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While whileStmt) {
        int initialId = nodeId;

        createNode("WHILE");
        createConnection(initialId, nodeId);
        whileStmt.condition.buildTree(this);
        createConnection(initialId, nodeId);
        whileStmt.body.buildTree(this);

        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If ifStmt) {
        int initialId = nodeId;

        createNode("IF");
        createConnection(initialId, nodeId);
        ifStmt.condition.buildTree(this);

        createConnection(initialId, nodeId);
        ifStmt.thenStmt.buildTree(this);

        if (ifStmt.elseStmt != null) {
            createConnection(initialId, nodeId);
            ifStmt.elseStmt.buildTree(this);
        }

        return null;
    }

    @Override
    public Void visitEnumStmt(Stmt.Enum enumStmt) {
        int initialId = nodeId;
        createNode("ENUM");

        for (Expr assignment : enumStmt.assignments)
        {
            createConnection(initialId, nodeId);
            assignment.buildTree(this);
        }

        return null;
    }

    @Override
    public Void visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        int initialId = nodeId;
        createNode("CONST FUNC " + constFunction.name);

        for (Token paramter : constFunction.parameters)
        {
            createConnection(initialId, nodeId);
            createNode(paramter);
        }

        for (Token returnValue : constFunction.returnValues)
        {
            createConnection(initialId, nodeId);
            createNode(returnValue);
        }

        createConnection(initialId, nodeId);
        constFunction.statements.buildTree(this);

        return null;
    }

    @Override
    public Void visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        int initialId = nodeId;
        createNode("MODULE FUNC " + moduleFunction.name);

        for (Token paramter : moduleFunction.parameters)
        {
            createConnection(initialId, nodeId);
            createNode(paramter);
        }

        for (Token returnValue: moduleFunction.returnValues)
        {
            createConnection(initialId, nodeId);
            createNode(returnValue);
        }

        createConnection(initialId, nodeId);
        moduleFunction.statements.buildTree(this);

        return null;
    }

    @Override
    public Void visitModuleStmt(Stmt.Module module)
    {
        int initialId = nodeId;

        createNode("MODULE " + module.name);
        createConnection(initialId, nodeId);
        module.expr.buildTree(this);

        return null;
    }

    @Override
    public Void visitConstStmt(Stmt.Const constStmt)
    {
        int initialId = nodeId;

        createNode("CONST " + constStmt.name);
        createConnection(initialId, nodeId);
        constStmt.expr.buildTree(this);

        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block block)
    {
        int initialId = nodeId;
        createNode("BLOCK");

        for (Stmt stmt : block.statements)
        {
            createConnection(initialId, nodeId);
            stmt.buildTree(this);
        }

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression expression)
    {
        createNode("EXPRESSION");
        createConnection(nodeId - 1, nodeId);
        expression.expr.buildTree(this);

        return null;
    }
}
