import java.util.ArrayList;
import java.util.List;

abstract class Stmt {
    abstract void print();

    abstract public <T> T evaluate(ASTVisitor<T> visitor);
    abstract public <T> T buildTree(ASTVisitor<T> visitor);

    static class Expression extends Stmt {
        Expr expr;

        Expression(Expr expr) {
            this.expr = expr;
        }

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitExpressionStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
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

        /*
        @Override
        void evaluate() {
            ConstEnvironment blockConstEnvironment = new ConstEnvironment();
            blockConstEnvironment.addEnclosing(Carmine.constEnvironment);

            Carmine.constEnvironment = blockConstEnvironment;

            for (Stmt stmt : statements) {
                stmt.evaluate();
            }

            Debug.environments.add(blockConstEnvironment);
            Carmine.constEnvironment = (ConstEnvironment) blockConstEnvironment.getEnclosing();
        }
         */

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class Const extends Stmt {
        final Token name;
        Expr expr;

        Const(Token name, Expr expr) {
            this.name = name;
            this.expr = expr;
        }

        /*
        @Override
        void evaluate() {
            ConstEnvironment env = Carmine.constEnvironment;
            while (env != null && !env.contains(name)) {
                env = (ConstEnvironment) env.getEnclosing();
            }

            if (env != null)
                throw new RuntimeException("Const " + name + " is already defined.");

            if (expr != null)
                Carmine.constEnvironment.put(name.lexeme, expr.evaluate());
            else
                Carmine.constEnvironment.put(name.lexeme, null);
        }
        */

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitConstStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitConstStmt(this);
        }
    }

    static class Module extends Stmt {
        final Token name;
        final Expr expr;

        Module(Token name, Expr expr) {
            this.name = name;
            this.expr = expr;
        }

        /*
        @Override
        void evaluate() {
            ModuleEnvironment env = Carmine.moduleEnvironment;
            while (env != null && !env.contains(name)) {
                env = (ModuleEnvironment) env.getEnclosing();
            }

            if (env != null)
                throw new RuntimeException("Module " + name + " is already defined.");

            if (expr != null)
                Carmine.constEnvironment.put(name.lexeme, expr.evaluate());
            else
                Carmine.constEnvironment.put(name.lexeme, null);
        }
*/
        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitModuleStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitModuleStmt(this);
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

        /*
        @Override
        void evaluate() {
        }
         */

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitModuleFunctionStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitModuleFunctionStmt(this);
        }
    }

    static class ConstFunction extends Stmt {
        final Token name;
        final List<Token> parameters;
        final List<Token> returnValues;
        final Stmt.Block statements;

        ConstFunction(Token name, List<Token> parameters, List<Token> returnValues, Stmt.Block statements) {
            this.name = name;
            this.parameters = parameters;
            this.statements = statements;
            this.returnValues = returnValues;
        }

        /*
        @Override
        void evaluate() {
        }*/

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitConstFunctionStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitConstFunctionStmt(this);
        }
    }

    static class Enum extends Stmt {
        Token name;
        final ArrayList<Expr> assignments;

        Enum(Token name, ArrayList<Expr> assignments) {
            this.name = name;
            this.assignments = assignments;
        }

        /*
        @Override
        void evaluate() {
        }*/

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitEnumStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
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

        /*
        @Override
        void evaluate() {
        }*/

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
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

        /*
        @Override
        void evaluate() {
        }*/

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    static class For extends Stmt {
        Expr init;
        Expr minValue;
        Expr maxValue;
        Stmt body;

        For(Expr init, Expr minValue, Expr maxValue, Stmt body) {
            this.init = init;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.body = body;
        }

        /*
        @Override
        void evaluate() {
        }*/

        @Override
        void print() {

        }

        @Override
        public <T> T evaluate(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
        @Override
        public <T> T buildTree(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }
}
