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
        final List<Expr> expressions;

        Block(List<Expr> expressions)
        {
            this.expressions = expressions;
        }

        @Override
        void evaluate()
        {
            for (Expr expr : expressions)
            {
                expr.evaluate();
            }
        }

        @Override
        void print()
        {
            for (Expr expr : expressions)
            {
                expr.print();
            }
        }

        List<Expr> getExpr()
        {
            return expressions;
        }
    }

    static class Variable extends Stmt
    {
        final String name;
        final Expr expr;

        Variable(String name, Expr expr)
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
