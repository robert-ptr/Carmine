import java.util.ArrayList;
import java.util.List;

abstract class Expr {
    public abstract <T> T print(ASTVisitor<T> visitor);
    public abstract <T> T fold(ASTVisitor<T> visitor);
    public abstract <T> T propagate(ASTVisitor<T> visitor); // propagate constants
    public abstract <T> T buildTree(ASTVisitor<T> visitor);

    public abstract int getLine();

    static class Binary extends Expr
    {
        Expr left;
        Expr right;
        Token operator;

        Binary(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        @Override
        public int getLine()
        {
            return operator.line;
        }
    }

    static class Unary extends Expr
    {
        Expr right;
        Token operator;

        Unary(Token operator, Expr right)
        {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        @Override
        public int getLine()
        {
            return operator.line;
        }
    }

    static class Variable extends Expr
    {
        Token name;

        Variable(Token name)
        {
            this.name = name;
        }

        public Token getName()
        {
            return name;
        }

        /*
        @Override
        Object evaluate() throws RuntimeException
        {
            if (Carmine.constEnvironment.contains(name))
                return Carmine.constEnvironment.get(name);
            else
                return Carmine.moduleEnvironment.get(name);
        }*/

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }
        @Override
        public int getLine()
        {
            return name.line;
        }
    }

    static class Assignment extends Expr
    {
        Token name;
        Expr right;

        Assignment(Token name, Expr right)
        {
            this.name = name;
            this.right = right;
        }

        public Token getName()
        {
            return name;
        }

        /*
        @Override
        Object evaluate()
        {
            ModuleEnvironment env = Carmine.moduleEnvironment;
            while (env != null && !env.contains(name)) {
                env = env.getEnclosing();

            }

            if (env == null)
                throw new RuntimeException(name.line + " Undefined variable: " + name);
            else {
                Object value = right.evaluate();

                env.put(name.lexeme, value);

                return value;
            }
        }*/

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }
        @Override
        public int getLine()
        {
            return name.line;
        }
    }

    static class Literal extends Expr
    {
        Object value;
        int line;

        Literal(int line, Object value)
        {
            this.line = line;
            this.value = value;
        }

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        @Override
        public int getLine()
        {
            return this.line;
        }
    }

    static class Group extends Expr
    {
        Expr expr;
        int line;

        Group(int line, Expr expr)
        {
            this.line = line;
            this.expr = expr;
        }

        /*
        @Override
        Object evaluate()
        {
            return expr.evaluate();
        }
    */

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
        @Override
        public int getLine()
        {
            return this.line;
        }
    }

    static class Call extends Expr
    {
        Expr callee; // test this
        List<Expr> arguments;

        int line;

        Call(int line, Expr callee, List<Expr> arguments)
        {
            this.line = line;
            this.callee = callee;
            this.arguments = arguments;
        }

        /*
        @Override
        Object evaluate()
        {
            Object obj = callee.evaluate();

            List<Object> args = new ArrayList<>();

            for (Expr arg : arguments)
            {
                args.add(arg.evaluate());
            }

            if (!(obj instanceof CarmineCallable))
            {
                throw new RuntimeException("Can only call functions and classes.");
            }

            CarmineCallable function = (CarmineCallable) obj;

            if (args.size() != function.arity())
            {
                throw new RuntimeException("Number of arguments does not match arity");
            }
            return function.call(args);
        }*/

        @Override
        public <T> T print(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
        @Override
        public <T> T fold(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
        @Override
        public <T> T propagate(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
        @Override
        public int getLine()
        {
            return this.line;
        }
    }
}
