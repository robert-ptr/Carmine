import java.util.ArrayList;
import java.util.List;

abstract class Stmt {
    abstract public <T> T accept(ASTVisitor<T> visitor);

    static class Expression extends Stmt {
        Expr expr;

        Expression(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        Expr getExpr() {
            return expr;
        }
    }

    static class Block extends Stmt {
        final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class ModuleFunction extends Stmt {
        final Token name;
        final List<Token> parameters;
        final List<Token> returnValues;
        final Stmt.Block statements;

        ModuleFunction(Token name, List<Token> parameters, List<Token> returnValues, Stmt.Block statements) {
            this.name = name;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitModuleFunctionStmt(this);
        }
    }

    static class VarFunction extends Stmt {
        final Token name;
        final List<Token> parameters;
        final List<Token> returnValues;
        final Stmt.Block statements;

        VarFunction(Token name, List<Token> parameters, List<Token> returnValues, Stmt.Block statements) {
            this.name = name;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitConstFunctionStmt(this); }
    }

    static class Enum extends Stmt {
        Token name;
        final ArrayList<Expr.Assignment> assignments;

        Enum(Token name, ArrayList<Expr.Assignment> assignments) {
            this.name = name;
            this.assignments = assignments;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitEnumStmt(this);
        }
    }

    static class If extends Stmt {
        Expr condition;
        Stmt thenStmt;
        Stmt elseStmt;

        If(Expr condition, Stmt thenStmt, Stmt elseStmt) {
            this.condition = condition;
            this.thenStmt = thenStmt;
            this.elseStmt = elseStmt;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class While extends Stmt {
        Expr condition;
        Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    static class For extends Stmt {
        Token var;
        Expr minValue;
        Expr maxValue;
        Stmt body;

        For(Token var, Expr minValue, Expr maxValue, Stmt body) {
            this.var = var;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.body = body;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }
}
