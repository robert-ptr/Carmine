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
                case WIRE: // WIP
                    return null;
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
}
