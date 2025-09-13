package carmine.compiler.structures;

import carmine.compiler.helpers.ASTVisitor;

import java.util.List;

public abstract class Expr {
    public abstract <T> T accept(ASTVisitor<T> visitor);

    public abstract int getLine();

    public static class Binary extends Expr
    {
        public Expr left;
        public Expr right;
        public Token operator;

        public Binary(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        @Override
        public int getLine()
        {
            return operator.getLine();
        }
    }

    public static class Unary extends Expr
    {
        public Expr right;
        public Token operator;

        public Unary(Token operator, Expr right)
        {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        @Override
        public int getLine()
        {
            return operator.getLine();
        }
    }

    public static class Identifier extends Expr
    {
        public Token name;

        public Identifier(Token name)
        {
            this.name = name;
        }

        public Token getName()
        {
            return name;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIdentifierExpr(this);
        }

        @Override
        public int getLine()
        {
            return name.getLine();
        }
    }

    public static class Module extends Expr
    {
        public Expr.Assignment assignment;

        public Module(Expr.Assignment assignment)
        {
            this.assignment = assignment;
        }

        public Token getName()
        {
            return assignment.getName();
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitModuleExpr(this);
        }

        @Override
        public int getLine()
        {
            return assignment.getName().getLine();
        }
    }

    public static class Variable extends Expr
    {
        public Expr.Assignment assignment;

        public Variable(Expr.Assignment assignment)
        {
            this.assignment = assignment;
        }

        public Token getName()
        {
            return assignment.getName();
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVarExpr(this);
        }

        @Override
        public int getLine()
        {
            return assignment.getName().getLine();
        }
    }

    public static class Assignment extends Expr
    {
        public Token name;
        public Expr right;

        public Assignment(Token name, Expr right) {
            this.name = name;
            this.right = right;
        }

        public Token getName()
        {
            return name;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }

        @Override
        public int getLine()
        {
            return name.getLine();
        }
    }

    public static class Literal extends Expr
    {
        public Object value;
        public int line;

        public Literal(int line, Object value)
        {
            this.line = line;
            this.value = value;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        @Override
        public int getLine()
        {
            return this.getLine();
        }
    }

    public static class Group extends Expr
    {
        public Expr expr;
        public int line;

        public Group(int line, Expr expr)
        {
            this.line = line;
            this.expr = expr;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }

        @Override
        public int getLine()
        {
            return this.getLine();
        }
    }

    public static class Call extends Expr
    {
        public Expr callee; // test this
        public List<Expr> arguments;

        public int line;

        public Call(int line, Expr callee, List<Expr> arguments)
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
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }

        @Override
        public int getLine()
        {
            return this.getLine();
        }
    }
}
