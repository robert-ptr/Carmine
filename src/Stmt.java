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
            Environment blockEnvironment = new Environment();
            blockEnvironment.addEnclosing(Carmine.environment);

            Carmine.environment = blockEnvironment;

            for (Stmt stmt : statements)
            {
                stmt.evaluate();
            }

            Debug.environments.add(blockEnvironment);
            Carmine.environment = blockEnvironment.getEnclosing();
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
            if (Carmine.environment.contains(name))
                throw new RuntimeException(name.line + " Variable already exists " + name); // this shows the wrong line. Maybe add something in the environment

            if (expr != null)
                Carmine.environment.put(name.lexeme, expr.evaluate());
            else
                Carmine.environment.put(name.lexeme, null);
        }

        @Override
        void print()
        {
            System.out.print("var " + name.lexeme + " ");
            if (expr != null) {
                System.out.print("= ");
                expr.print();
            }
            else
            {
                System.out.println();
            }
        }
    }

    static class Assignment extends Stmt
    {
        final Token name;
        final Expr expr;

        Assignment(Token name, Expr expr)
        {
            this.name = name;
            this.expr = expr;
        }

        @Override
        void evaluate()
        {
            if (!Carmine.environment.contains(name))
                throw new RuntimeException(name.line + " Undefined variable: " + name);

            Carmine.environment.put(name.lexeme, expr.evaluate());
        }

        @Override
        void print()
        {
            System.out.println(name.lexeme + " ");
            expr.print();
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
