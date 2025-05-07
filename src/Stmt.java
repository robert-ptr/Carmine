import java.util.List;

abstract class Stmt {
    abstract void evaluate();

    abstract void print();

    static class Expression extends Stmt
    {
        final Expr expr;
        Expression(Expr expr)
        {
            this.expr = expr;
        }

        @Override
        void evaluate()
        {
            expr.evaluate();
        }

        @Override
        void print()
        {
            expr.print();
        }

        Expr getExpr()
        {
            return expr;
        }
    }

    static class Block extends Stmt
    {
        final List<Stmt> statements;

        Block(List<Stmt> statements)
        {
            this.statements = statements;
        }

        @Override
        void evaluate()
        {
            for (Stmt stmt : statements)
            {
                stmt.evaluate();
            }
        }

        @Override
        void print()
        {
            for (Stmt stmt : statements)
            {
                stmt.print();
                System.out.println();
            }
        }
    }

    static class Variable extends Stmt
    {
        final Token name;
        final Expr expr;

        Variable(Token name, Expr expr)
        {
            this.name = name;
            this.expr = expr;
        }

        @Override
        void evaluate()
        {
        }

        @Override
        void print()
        {
        }
    }

    static class Assignment extends Stmt
    {
        final String name;
        final Expr expr;

        Assignment(String name, Expr expr)
        {
            this.name = name;
            this.expr = expr;
        }

        @Override
        void evaluate()
        {
        }

        @Override
        void print()
        {
        }
    }

    static class Function extends Stmt
    {
        @Override
        void evaluate()
        {
        }

        @Override
        void print()
        {
        }
    }

    static class Main extends Stmt
    {
        @Override
        void evaluate()
        {
        }

        @Override
        void print()
        {
        }
    }
}
