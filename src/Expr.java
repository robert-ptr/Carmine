import java.util.ArrayList;
import java.util.List;

abstract class Expr {
    abstract Object evaluate();  // expressions return objects, because they produce values, statements don't(but they do have side effects).

    abstract void print();

    static class Binary extends Expr
    {
        final Expr left;
        final Expr right;
        final Token operator;

        Binary(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        Object evaluate()
        {
            Object op1 = left.evaluate();
            Object op2 = right.evaluate();

            switch(operator.type)
            {
                case OR:
                    return (boolean)op1 || (boolean)op2;
                case AND:
                    return (boolean)op1 && (boolean)op2;
                default:
                    Carmine.error("Unknown binary operator: " + operator);
                    return null;
            }
        }

        @Override
        void print()
        {
            left.print();
            System.out.print(" " + operator.lexeme + " ");
            right.print();
            System.out.print(" ");
        }
    }

    static class Unary extends Expr
    {
        final Expr right;
        final Token operator;

        Unary(Token operator, Expr right)
        {
            this.operator = operator;
            this.right = right;
        }

        @Override
        Object evaluate()
        {
            Object op = right.evaluate();
            switch(operator.type)
            {
                case NOT:
                    return !(boolean)op;
                default:
                    Carmine.error("Unknown unary operator: " + operator);
                    return null;
            }
        }

        @Override
        void print()
        {
            System.out.print(operator.lexeme + " ");
            right.print();
        }
    }

    static class Variable extends Expr
    {
        final Token name;

        Variable(Token name)
        {
            this.name = name;
        }

        public Token getName()
        {
            return name;
        }

        @Override
        Object evaluate() throws RuntimeException
        {
            return Carmine.constEnvironment.get(name.lexeme);
        }

        @Override
        void print()
        {
            System.out.print(name.lexeme);
        }
    }

    static class Assignment extends Expr
    {
        final Token name;
        final Expr right;

        Assignment(Token name, Expr right)
        {
            this.name = name;
            this.right = right;
        }

        public Token getName()
        {
            return name;
        }

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
        }

        @Override
        void print()
        {
            System.out.println(name.lexeme + " ");
            right.print();
        }
    }

    static class Literal extends Expr
    {
        final Object value;

        Literal(Token value)
        {
            this.value = value.value;
        }

        Literal(Object value)
        {
            this.value = value;
        }

        @Override
        Object evaluate()
        {
            return value;
        }

        @Override
        void print()
        {
            System.out.print(value.toString());
        }
    }

    static class Group extends Expr
    {
        final Expr expr;

        Group(Expr expr)
        {
            this.expr = expr;
        }

        @Override
        Object evaluate()
        {
            return expr.evaluate();
        }

        @Override
        void print()
        {
            System.out.print("(");
            expr.print();
            System.out.print(")");
        }
    }

    static class Call extends Expr
    {
        final Expr callee;
        final List<Expr> arguments;

        Call(Expr callee, List<Expr> arguments)
        {
            this.callee = callee;
            this.arguments = arguments;
        }

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
        }

        @Override
        void print()
        {
            callee.print();
            System.out.print("(");
            for (Expr arg : arguments)
            {
                arg.print();
                System.out.print(", ");
            }

            System.out.print(")");
        }
    }
}
