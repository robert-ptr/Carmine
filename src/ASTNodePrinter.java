public class ASTNodePrinter implements ASTVisitor<Void> {
    public Void visitLiteralExpr(Expr.Literal expr) {
        if (expr.value != null)
            System.out.print(expr.value);
        else
            System.out.print("null");

        return null;
    }

    public Void visitUnaryExpr(Expr.Unary expr)
    {
        System.out.print(expr.operator.lexeme + " ");
        expr.right.print(this);

        return null;
    }

    public Void visitBinaryExpr(Expr.Binary expr)
    {
        expr.left.print(this);
        System.out.print(" " + expr.operator.lexeme + " ");
        expr.right.print(this);
        System.out.print(" ");

        return null;
    }

    public Void visitCallExpr(Expr.Call call)
    {
        call.callee.print(this);
        System.out.print("(");
        for (int i = 0; i < call.arguments.size(); i++)
        {
            call.arguments.get(i).print(this);
            if (i < call.arguments.size() - 1)
                System.out.print(", ");
        }

        System.out.print(")");

        return null;
    }

    public Void visitGroupExpr(Expr.Group group)
    {
        System.out.print("(");
        group.expr.print(this);
        System.out.print(")");

        return null;
    }

    public Void visitAssignmentExpr(Expr.Assignment assignment)
    {
        System.out.print(assignment.name.lexeme + " = ");
        assignment.right.print(this);
        System.out.println();

        return null;
    }

    public Void visitVariableExpr(Expr.Variable variable)
    {
        System.out.print(variable.name.lexeme);

        return null;
    }

    public Void visitForStmt(Stmt.For forStmt)
    {
        System.out.println("for ");
        forStmt.init.print(this);
        System.out.print("");
        forStmt.minValue.print(this);
        System.out.print("..");
        forStmt.maxValue.print(this);
        System.out.println();
        forStmt.body.print(this);

        return null;
    }

    public Void visitWhileStmt(Stmt.While whileStmt)
    {
        System.out.print("while ");
        whileStmt.condition.print(this);
        System.out.println();
        whileStmt.body.print(this);

        return null;
    }

    public Void visitIfStmt(Stmt.If ifStmt)
    {
        System.out.print("if ");
        ifStmt.condition.print(this);
        System.out.println();
        ifStmt.thenStmt.print(this);

        if (ifStmt.elseStmt != null) {
            System.out.print("else ");
            ifStmt.elseStmt.print(this);
        }

        return null;
    }

    public Void visitEnumStmt(Stmt.Enum enumStmt)
    {
        System.out.print("enum ");
        if (enumStmt.name != null)
            System.out.println(enumStmt.name.lexeme);
        System.out.println("{");
        for (int i = 0; i < enumStmt.assignments.size(); i++)
            enumStmt.assignments.get(i).print(this);
        System.out.println("};");

        return null;
    }

    public Void visitConstFunctionStmt(Stmt.ConstFunction constFunction)
    {
        System.out.print("def ");
        System.out.print(constFunction.name.lexeme);
        System.out.printf("(");

        for (int i = 0; i < constFunction.parameters.size(); i++) {
            System.out.printf(constFunction.parameters.get(i).lexeme);

            if (i < constFunction.parameters.size() - 1)
                System.out.print(", ");
        }

        System.out.print(") ");

        if (constFunction.returnValues.size() > 0)
            System.out.print("-> ");

        for (int i = 0; i < constFunction.returnValues.size(); i++) {
            System.out.print(constFunction.returnValues.get(i).lexeme);

            if (i < constFunction.returnValues.size() - 1)
                System.out.print(", ");
        }

        System.out.println();
        constFunction.statements.print(this);

        return null;
    }

    public Void visitModuleFunctionStmt(Stmt.ModuleFunction moduleFunction)
    {
        System.out.print("def ");
        System.out.print(moduleFunction.name.lexeme);
        System.out.printf("(");

        for (int i = 0; i < moduleFunction.parameters.size(); i++) {
            System.out.printf(moduleFunction.parameters.get(i).lexeme);

            if (i < moduleFunction.parameters.size() - 1)
                System.out.print(", ");
        }

        System.out.print(") ");

        if (moduleFunction.returnValues.size() > 0)
            System.out.print("-> ");

        for (int i = 0; i < moduleFunction.returnValues.size(); i++) {
            System.out.print(moduleFunction.returnValues.get(i).lexeme);

            if (i < moduleFunction.returnValues.size() - 1)
                System.out.print(", ");
        }

        System.out.println();
        moduleFunction.statements.print(this);

        return null;
    }

    public Void visitModuleStmt(Stmt.Module module)
    {
        System.out.print("module " + module.name.lexeme + " ");
        if (module.expr != null) {
            System.out.print("= ");
            module.expr.print(this);
            System.out.print(";");
        } else {
            System.out.println(";");
        }

        return null;
    }

    public Void visitConstStmt(Stmt.Const constStmt)
    {
        System.out.print("const " + constStmt.name.lexeme + " ");
        if (constStmt.expr != null) {
            System.out.print("= ");
            constStmt.expr.print(this);
            System.out.print(";");
        } else {
            System.out.println(";");
        }

        return null;
    }

    public Void visitBlockStmt(Stmt.Block block)
    {
        System.out.println("{");
        for (Stmt stmt : block.statements) {
            stmt.print(this);
            System.out.println();
        }
        System.out.println("}");

        return null;
    }

    public Void visitExpressionStmt(Stmt.Expression expression)
    {
        expression.expr.print(this);

        return null;
    }
}
