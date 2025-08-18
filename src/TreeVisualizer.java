import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeVisualizer implements ASTVisitor<Void> {
    public static List<VariableEnvironment> environments = new ArrayList<VariableEnvironment>();

    private int nodeId = 0;
    private StringBuilder builder = new StringBuilder();

    public static void printEnvironments() {
        environments.add(Carmine.variableEnvironment);

        for (VariableEnvironment variableEnvironment : environments) {
            System.out.println("environment");
            HashMap<String, Object> variables = variableEnvironment.getVariables();

            for (String key : variables.keySet()) {
                System.out.println(key + ": " + variables.get(key));
            }
        }
    }

    public String visualizeAST(List<Stmt> statements) {
        builder.append("digraph AST {\n");

        for (Stmt stmt : statements)
        {
            stmt.accept(this);
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
    public Void visitUnaryExpr(Expr.Unary expr) {
        createNode(expr.operator);
        createConnection(nodeId - 1, nodeId);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        int initialId = nodeId;
        createNode(expr.operator);

        createConnection(initialId, nodeId);

        expr.left.accept(this);
        createConnection(initialId, nodeId);
        expr.right.accept(this);

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
        group.expr.accept(this);

        return null;
    }

    @Override
    public Void visitAssignmentExpr(Expr.Assignment assignment) {
        createNode("ASSIGNMENT " + assignment.name);
        createConnection(nodeId - 1, nodeId);
        assignment.right.accept(this);

        return null;
    }

    @Override
    public Void visitIdentifierExpr(Expr.Identifier variable) {
        createNode(variable.name);

        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For forStmt) { // TO DO
        int initialId = nodeId;

        createNode("FOR");
        createConnection(initialId, nodeId);
        forStmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While whileStmt) {
        int initialId = nodeId;

        createNode("WHILE");
        createConnection(initialId, nodeId);
        whileStmt.condition.accept(this);
        createConnection(initialId, nodeId);
        whileStmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If ifStmt) {
        int initialId = nodeId;

        createNode("IF");
        createConnection(initialId, nodeId);
        ifStmt.condition.accept(this);

        createConnection(initialId, nodeId);
        ifStmt.thenStmt.accept(this);

        if (ifStmt.elseStmt != null) {
            createConnection(initialId, nodeId);
            ifStmt.elseStmt.accept(this);
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
            assignment.accept(this);
        }

        return null;
    }

    @Override
    public Void visitConstFunctionStmt(Stmt.VarFunction varFunction)
    {
        int initialId = nodeId;
        createNode("CONST FUNC " + varFunction.name);

        for (Token paramter : varFunction.parameters)
        {
            createConnection(initialId, nodeId);
            createNode(paramter);
        }

        for (Token returnValue : varFunction.returnValues)
        {
            createConnection(initialId, nodeId);
            createNode(returnValue);
        }

        createConnection(initialId, nodeId);
        varFunction.statements.accept(this);

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
        moduleFunction.statements.accept(this);

        return null;
    }

    @Override
    public Void visitModuleExpr(Expr.Module module)
    {
        int initialId = nodeId;

        createNode("MODULE " + module.getName());
        createConnection(initialId, nodeId);
        module.assignment.accept(this);

        return null;
    }

    @Override
    public Void visitVarExpr(Expr.Variable var) {
        int initialId = nodeId;

        createNode("CONST " + var.getName());
        createConnection(initialId, nodeId);
        var.assignment.accept(this);

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
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression expression)
    {
        createNode("EXPRESSION");
        createConnection(nodeId - 1, nodeId);
        expression.expr.accept(this);

        return null;
    }
}
