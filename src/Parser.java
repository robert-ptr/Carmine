import java.util.ArrayList;
import java.util.List;

public class Parser
{
    private boolean mainFound = false;
    List<Token> tokens = new ArrayList<Token>();
    int current = 0;
    boolean hadError = false;

    private boolean isAtEnd()
    {
        return peek().type == TokenType.EOF;
    }

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token advance()
    {
        if (isAtEnd())
            return null;

        return tokens.get(current++);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private boolean check(TokenType type)
    {
        if (isAtEnd())
            return false;

        return peek().type == type;
    }

    private boolean match(TokenType... types)
    {
        for (TokenType type : types) {
            if (peek().type == type) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Expr expression()
    {
        Expr left = or();

        while (match(TokenType.ASSIGN))
        {
            Expr right = expression();

            if (left instanceof Expr.Variable)
            {
                return new Expr.Assignment(((Expr.Variable)left).getName(), right);
            }

            Carmine.error(peek().line + " Invalid assignment target.");
        }

        return left;
    }

    private Expr or()
    {
        Expr expr = and();

        while (match(TokenType.OR))
        {
            Token op = previous();
            Expr right = and();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr and()
    {
        Expr expr = equality();

        while (match(TokenType.AND))
        {
            Token op = previous();
            Expr right = equality();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr equality()
    {
        Expr expr = comparison();

        while (match(TokenType.EQUAL) || match(TokenType.NOTEQUAL))
        {
            Token op = previous();
            Expr right = comparison();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr comparison()
    {
        Expr expr = term();

        while (match(TokenType.GREATER) || match(TokenType.LESS) || match(TokenType.GREATER_EQUAL) || match(TokenType.LESS_EQUAL))
        {
            Token op = previous();
            Expr right = term();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr term()
    {
        Expr expr = factor();

        while (match(TokenType.MINUS) || match(TokenType.PLUS))
        {
            Token op = previous();
            Expr right = factor();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr factor()
    {
        Expr expr = unary();

        while (match(TokenType.DIV) || match(TokenType.MOD) || match(TokenType.MUL))
        {
            Token op = previous();
            Expr right = unary();

            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    private Expr unary()
    {
        if (match(TokenType.NOT))
        {
            Token op = previous();
            Expr right = expression();

            return new Expr.Unary(op, right);
        }

        return call();
    }

    private List<Expr> arguments()
    {
        List<Expr> args = new ArrayList<>();

        if (!match(TokenType.RPAREN))
        {
            do
            {
                args.add(expression());
            } while (match(TokenType.COMMA));

            if (!match(TokenType.RPAREN))
                Carmine.error(peek().line + " Expected ')'.");
        }

        return args;
    }

    private Expr call()
    {
        Expr expr = primary();

        if (match(TokenType.LPAREN))
        {
            List<Expr> args = arguments();

            return new Expr.Call(expr, args);
        }
        else
        {
            return expr;
        }
    }

    private Expr primary()
    {
        if (match(TokenType.TRUE))
            return new Expr.Literal(true);

        if (match(TokenType.FALSE))
            return new Expr.Literal(false);

        if (match(TokenType.LPAREN))
        {
            Expr expr = expression();
            if (!match(TokenType.RPAREN))
            {
                Carmine.error(peek().line + "Expected matching ')' for '('.");
            }

            return new Expr.Group(expr);
        }

        if (match(TokenType.DECIMAL))
        {
            return new Expr.Literal(Integer.parseInt(previous().lexeme));
        }

        if (match(TokenType.HEXADECIMAL))
        {
            return new Expr.Literal(Integer.parseInt(previous().lexeme, 16));
        }

        if (match(TokenType.BINARY))
        {
            return new Expr.Literal(Integer.parseInt(previous().lexeme, 2));
        }

        if (match(TokenType.IDENTIFIER))
        {
            return new Expr.Variable(previous());
        }

        Carmine.error(peek().line + " Unexpected token: " + peek());
        hadError = true;
        return null;
    }

    private Stmt expressionStmt()
    {
        Expr expr = expression();
        /*
        if (!match(TokenType.ENDLINE) && !match(TokenType.EOF))
        {
            hadError = true;
            Carmine.error(peek().line + " Invalid expression.");
        }
         */

        return new Stmt.Expression(expr);
    }

    private Stmt moduleStatement()
    {
        Expr left = expression();

        if (match(TokenType.ASSIGN))
        {
            Expr right = expression();
            match(TokenType.ENDLINE);
            if (left instanceof Expr.Variable)
            {

                return new Stmt.Module(((Expr.Variable)left).getName(), right);
            }

            Carmine.error(peek().line + "Invalid variable.");
            hadError = true;

            return null;
        }

        match(TokenType.ENDLINE);
        if (left instanceof Expr.Variable)
        {
            return new Stmt.Module(((Expr.Variable)left).getName(), null);
        }

        Carmine.error(peek().line + "Invalid variable.");
        hadError = true;
        
        return null;
    }

    private List<Stmt> blockStatement()
    {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd() && !match(TokenType.RBRACE))
        {
            statements.add(statement());
        }
        match(TokenType.ENDLINE);
        //return new Stmt.Block(statements);
        return statements;
    }

    private Stmt functionStatement()
    {
        Expr left = expression();

        List<Token> params = new ArrayList<>();
        List<Token> returnValues = new ArrayList<>();

        match(TokenType.LPAREN);

        if (!match(TokenType.RPAREN))
        {
            do
            {
                params.add(advance());
            } while(match(TokenType.COMMA));
        }

        if (!match(TokenType.RPAREN))
            Carmine.error(peek().line + "Expected ')'.");

        if (match(TokenType.ARROW)) // then it returns one or multiple values
        {
            do
            {
                returnValues.add(advance());
            } while(match(TokenType.COMMA));
        }

        List<Stmt> statements = blockStatement();

        return new Stmt.Function(left, params, returnValues, statements);
    }

    private Stmt mainStatement()
    {
        if (!mainFound)
        {
            mainFound = true;
        }
        else
        {
            Carmine.error(peek().line + "Redefinition of main statement is not allowed.");
            return null;
        }
        
        match(TokenType.LPAREN);

        if (!match(TokenType.RPAREN))
        {
            Carmine.error(peek().line + "The main function does not have parameters.");
        }

        if (!match(TokenType.RPAREN))
            Carmine.error(peek().line + "Expected ')'.");

        if (match(TokenType.ARROW)) // then it returns one or multiple values
        {
            Carmine.error(peek().line + "The main function does not have return values.");
        }

        List<Stmt> statements = blockStatement();

        return new Stmt.Main(statements);
    }

    private Stmt constStatement()
    {
        Expr left = expression();

        if (match(TokenType.ASSIGN))
        {
            Expr right = expression();
            match(TokenType.ENDLINE);
            if (left instanceof Expr.Variable)
            {
                return new Stmt.Const(((Expr.Variable)left).getName(), right);
            }

            Carmine.error(peek().line + "Invalid variable.");
            hadError = true;

            return null;
        }

        match(TokenType.ENDLINE);
        if (left instanceof Expr.Variable)
        {
            return new Stmt.Const(((Expr.Variable)left).getName(), null);
        }

        Carmine.error(peek().line + "Invalid variable.");
        hadError = true;

        return null;
    }

    private Stmt enumStatement()
    {
        boolean found_brace = false;
        match(TokenType.ENDLINE);
        if (!match(TokenType.LBRACE))
            Carmine.error(peek().line + " Expected '{'.");

        ArrayList<Expr> assignments = new ArrayList<>();
        do
        {
            match(TokenType.ENDLINE);
            if (match(TokenType.RBRACE)) {
                found_brace = true;
                break;
            }
            Expr assignment = expression();
            if (!(assignment instanceof Expr.Assignment))
            {
                Carmine.error(peek().line + " Invalid assignment.");
            }
            assignments.add(assignment);
        } while (match(TokenType.COMMA));

        if (!match(TokenType.RBRACE) && !found_brace)
            Carmine.error(peek().line + " Expected '}'.");

        return new Stmt.Enum(assignments);
    }

    private Stmt ifStatement()
    {
        Expr condition = expression();

        Stmt thenBranch = statement();
        if (match(TokenType.ELSE))
        {
            Stmt elseBranch;
            if (match(TokenType.IF))
            {
                elseBranch = ifStatement();
            }
            else
            {
                elseBranch = statement();
            }

            return new Stmt.If(condition, thenBranch, elseBranch);
        }

        return new Stmt.If(condition, thenBranch, null);
    }

    private Stmt whileStatement()
    {
        Expr condition = expression();

        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt forStatement() // unused right now, still unsure about for syntax
    {
        Expr init = expression();

        if (!(init instanceof Expr.Variable))
        {
            Carmine.error(peek().line + "Invalid variable.");
        }

        if (!(peek().lexeme.contentEquals("in")))
            Carmine.error(peek().line + "Missing 'in' keyword.");

        advance();

        Expr minValue = expression();

        if (!match(TokenType.DOT))
        {

        }
        if (!match(TokenType.DOT))
        {
            Carmine.error(peek().line + "Missing '..' keyword.");
        }

        Expr maxValue = expression();

        Stmt body = statement();
        return new Stmt.For(init, minValue, maxValue, body);
    }

    private Stmt declaration()
    {
        while (match(TokenType.ENDLINE)) ;

        if (match(TokenType.MODULE))
            return moduleStatement();
        else if (match(TokenType.CONST))
            return constStatement();
        else if (match(TokenType.DEF))
        {
            if (match(TokenType.MAIN))
            {
                return mainStatement();
            }

            return functionStatement();
        }
        else if (match(TokenType.ENUM))
            return enumStatement();
        else
            return null;
    }

    private Stmt statement() {
        if (match(TokenType.LBRACE))
        {
            match(TokenType.ENDLINE);
            return new Stmt.Block(blockStatement());
        }
        else if (match(TokenType.IF))
        {
            return ifStatement();
        } else if (match(TokenType.WHILE))
        {
            return whileStatement();
        }
        else if (match(TokenType.FOR))
        {
            return forStatement();
        }

        return expressionStmt();
    }

    public List<Stmt> parse()
    {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd())
        {
            statements.add(declaration());
        }

        return statements;
    }
}
