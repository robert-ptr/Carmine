package carmine.compiler.structures;

import carmine.compiler.helpers.AstVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class Stmt {
    public abstract<T> T accept(AstVisitor<T> visitor);

    public static class Expression extends Stmt {
        public Expr expr;

        public Expression(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Block extends Stmt {
        public final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class ModuleFunction extends Stmt {
        public final Token name;
        public final List<Token> parameters;
        public final List<Token> returnValues;
        public final Stmt.Block statements;

        public ModuleFunction(Token name, List<Token> parameters, List<Token> returnValues, Stmt.Block statements) {
            this.name = name;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitModuleFunctionStmt(this);
        }
    }

    public static class VarFunction extends Stmt {
        public final Token name;
        public final List<Token> parameters;
        public final List<Token> returnValues;
        public final Stmt.Block statements;

        public VarFunction(Token name, List<Token> parameters, List<Token> returnValues, Stmt.Block statements) {
            this.name = name;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) { return visitor.visitConstFunctionStmt(this); }
    }

    public static class Enum extends Stmt {
        public Token name;
        public final ArrayList<Expr.Assignment> assignments;

        public Enum(Token name, ArrayList<Expr.Assignment> assignments) {
            this.name = name;
            this.assignments = assignments;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitEnumStmt(this);
        }
    }

    public static class If extends Stmt {
        public Expr condition;
        public Stmt thenStmt;
        public Stmt elseStmt;

        public If(Expr condition, Stmt thenStmt, Stmt elseStmt) {
            this.condition = condition;
            this.thenStmt = thenStmt;
            this.elseStmt = elseStmt;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        public Expr condition;
        public Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class For extends Stmt {
        public Token var;
        public Expr minValue;
        public Expr maxValue;
        public Stmt body;

        public For(Token var, Expr minValue, Expr maxValue, Stmt body) {
            this.var = var;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.body = body;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }
}
