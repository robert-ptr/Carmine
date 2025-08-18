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
        expr.right.accept(this);

        return null;
    }

    public Void visitBinaryExpr(Expr.Binary expr)
    {
        expr.left.accept(this);
        System.out.print(" " + expr.operator.lexeme + " ");
        expr.right.accept(this);
        System.out.print(" ");

        return null;
    }

    public Void visitCallExpr(Expr.Call call)
    {
        call.callee.accept(this);
        System.out.print("(");
        for (int i = 0; i < call.arguments.size(); i++)
        {
            call.arguments.get(i).accept(this);
            if (i < call.arguments.size() - 1)
                System.out.print(", ");
        }

        System.out.print(")");

        return null;
    }

    public Void visitGroupExpr(Expr.Group group)
    {
        System.out.print("(");
        group.expr.accept(this);
        System.out.print(")");

        return null;
    }

    public Void visitAssignmentExpr(Expr.Assignment assignment)
    {
        System.out.print(assignment.name.lexeme + " = ");
        assignment.right.accept(this);
        System.out.println();

        return null;
    }

    @Override
    public Void visitIdentifierExpr(Expr.Identifier identifier) {
        return null;
    }

    public Void visitForStmt(Stmt.For forStmt)
    {
        System.out.println("for ");
        System.out.print(forStmt.var.lexeme);
        System.out.print("");
        forStmt.minValue.accept(this);
        System.out.print("..");
        forStmt.maxValue.accept(this);
        System.out.println();
        forStmt.body.accept(this);

        return null;
    }

    public Void visitWhileStmt(Stmt.While whileStmt)
    {
        System.out.print("while ");
        whileStmt.condition.accept(this);
        System.out.println();
        whileStmt.body.accept(this);

        return null;
    }

    public Void visitIfStmt(Stmt.If ifStmt)
    {
        System.out.print("if ");
        ifStmt.condition.accept(this);
        System.out.println();
        ifStmt.thenStmt.accept(this);

        if (ifStmt.elseStmt != null) {
            System.out.print("else ");
            ifStmt.elseStmt.accept(this);
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
            enumStmt.assignments.get(i).accept(this);
        System.out.println("};");

        return null;
    }

    public Void visitConstFunctionStmt(Stmt.VarFunction varFunction)
    {
        System.out.print("def ");
        System.out.print(varFunction.name.lexeme);
        System.out.printf("(");

        for (int i = 0; i < varFunction.parameters.size(); i++) {
            System.out.printf(varFunction.parameters.get(i).lexeme);

            if (i < varFunction.parameters.size() - 1)
                System.out.print(", ");
        }

        System.out.print(") ");

        if (varFunction.returnValues.size() > 0)
            System.out.print("-> ");

        for (int i = 0; i < varFunction.returnValues.size(); i++) {
            System.out.print(varFunction.returnValues.get(i).lexeme);

            if (i < varFunction.returnValues.size() - 1)
                System.out.print(", ");
        }

        System.out.println();
        varFunction.statements.accept(this);

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
        moduleFunction.statements.accept(this);

        return null;
    }

    public Void visitModuleExpr(Expr.Module module)
    {
        System.out.print("module " + module.getName().lexeme + " ");
        if (module.assignment != null) {
            System.out.print("= ");
            module.assignment.accept(this);
            System.out.print(";");
        } else {
            System.out.println(";");
        }

        return null;
    }

    public Void visitVarExpr(Expr.Variable var)
    {
        System.out.print("const " + var.getName().lexeme + " ");
        if (var.assignment != null) {
            System.out.print("= ");
            var.assignment.accept(this);
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
            stmt.accept(this);
            System.out.println();
        }
        System.out.println("}");

        return null;
    }

    public Void visitExpressionStmt(Stmt.Expression expression)
    {
        expression.expr.accept(this);

        return null;
    }
}
