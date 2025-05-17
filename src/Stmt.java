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
            ConstEnvironment blockConstEnvironment = new ConstEnvironment();
            blockConstEnvironment.addEnclosing(Carmine.constEnvironment);

            Carmine.constEnvironment = blockConstEnvironment;

            for (Stmt stmt : statements)
            {
                stmt.evaluate();
            }

            Debug.environments.add(blockConstEnvironment);
            Carmine.constEnvironment = (ConstEnvironment) blockConstEnvironment.getEnclosing();
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
            ConstEnvironment env = Carmine.constEnvironment;
            while (env != null && !env.contains(name)) {
                env = (ConstEnvironment)env.getEnclosing();
            }

            if (env != null)
                throw new RuntimeException("Variable " + name + " is already defined.");

            if (expr != null)
                Carmine.constEnvironment.put(name.lexeme, expr.evaluate());
            else
                Carmine.constEnvironment.put(name.lexeme, null);
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

    static class Module extends Stmt
    {
        final Token name;
        final Expr expr;

        Module(Token name, Expr expr)
        {
            this.name = name;
            this.expr = expr;
        }

        @Override
        void evaluate()
        {
            ModuleEnvironment env = Carmine.moduleEnvironment;
            while (env != null && !env.contains(name)) {
                env = (ModuleEnvironment)env.getEnclosing();
            }

            if (env != null)
                throw new RuntimeException("Variable " + name + " is already defined.");

            if (expr != null)
                Carmine.constEnvironment.put(name.lexeme, expr.evaluate());
            else
                Carmine.constEnvironment.put(name.lexeme, null);
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

    static class Function extends Stmt
    {
        final Expr function;
        final List<Token> parameters;
        final List<Token> returnValues;
        final List<Stmt> statements;

        Function(Expr function, List<Token> parameters, List<Token> returnValues, List<Stmt> statements)
        {
            this.function = function;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
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

    static class Main extends Stmt
    {
        final List<Stmt> statements;

        Main(List<Stmt> statements)
        {
            this.statements = statements;
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
}
