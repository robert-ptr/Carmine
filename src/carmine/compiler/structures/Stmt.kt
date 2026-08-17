package carmine.compiler.structures;

import carmine.compiler.helpers.AstVisitor;

import java.util.ArrayList;

abstract class Stmt {
    abstract fun <T>  accept(visitor : AstVisitor<T>) : T

    class Expression(val expr : Expr?) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitExpressionStmt(this)
        }
    }

    class Block(val statements : List<Stmt?>) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitBlockStmt(this)
        }
    }

    class ModuleFunction(val name : Token,
                         val parameters : List<Token?>,
                         val returnValues : List<Token?>,
                         val statements : Block) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitModuleFunctionStmt(this)
        }
    }

    class VarFunction(val name : Token,
                      val parameters : List<Token?>,
                      val returnValues : List<Token?>,
                      val statements : Block) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitConstFunctionStmt(this)
        }
    }

    class Enum(val name : Token?, val assignments : ArrayList<Expr.Assignment?>) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitEnumStmt(this)
        }
    }

    class If(val condition : Expr?, val thenStmt : Stmt, val elseStmt : Stmt?) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitIfStmt(this)
        }
    }

    class While(val condition : Expr?, val body : Stmt) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitWhileStmt(this)
        }
    }

    class For(val varToken : Token,
              val minValue : Expr?,
              val maxValue : Expr?,
              val body : Stmt) : Stmt() {
        override fun <T> accept(visitor: AstVisitor<T>): T {
            return visitor.visitForStmt(this)
        }
    }
}
