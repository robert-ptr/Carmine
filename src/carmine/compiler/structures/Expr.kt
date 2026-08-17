package carmine.compiler.structures;

import carmine.compiler.helpers.AstVisitor;

abstract class Expr {
    abstract fun <T> accept(visitor : AstVisitor<T>) : T

    abstract val line : Int

    class Binary(var left : Expr?, val op : Token, var right : Expr?) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitBinaryExpr(this)
        }

        override val line : Int
            get() = op.line

        override fun toString() : String
        {
            return "$left $op $right"
        }
    }

    class Unary(val op : Token, var right : Expr?) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitUnaryExpr(this)
        }

        override val line : Int
            get() = op.line

        override fun toString() : String
        {
            return "$op $right"
        }
    }

    class Identifier(val name : Token) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitIdentifierExpr(this)
        }

        override val line : Int
            get() = name.line

        override fun toString() : String
        {
            return name.lexeme
        }
    }

    class Module(val assignment: Assignment) : Expr()
    {
        val name : Token
            get() = assignment.name

        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitModuleExpr(this)
        }

        override val line : Int
            get() = name.line

        override fun toString() : String
        {
            return "module  + $assignment"
        }
    }

    class Variable(val assignment : Assignment) : Expr()
    {
        val name : Token
            get() = assignment.name

        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitVarExpr(this)
        }

        override val line : Int
            get() = name.line

        override fun toString() : String
        {
            return "variable  + $assignment"
        }
    }

    class Assignment(val name : Token, var right : Expr?) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitAssignmentExpr(this)
        }

        override val line : Int
            get() = name.line

        override fun toString() : String
        {
            return "${name.lexeme} = $right"
        }
    }

    class Literal(override val line : Int, val objVal : Any?) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitLiteralExpr(this)
        }

        override fun toString() : String
        {
            return "$objVal"
        }
    }

    class Group(override val line : Int, var expr : Expr?) : Expr()
    {
        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitGroupExpr(this)
        }

        override fun toString() : String
        {
            return "( + $expr + )"
        }
    }

    class Call(override val line : Int, val callee : Expr?, val arguments : List<Expr?>) : Expr()
    {
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

        override fun <T> accept(visitor : AstVisitor<T>) : T {
            return visitor.visitCallExpr(this)
        }

        override fun toString() : String
        {
            return "$callee ( + $arguments + )"
        }
    }
}
