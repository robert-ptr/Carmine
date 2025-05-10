import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingPermission;

public class Parser
{
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

    private Expr assignment()
    {
        Expr left = expression();

        if (match(TokenType.EQUAL))
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

    private Expr expression()
    {
        return or();
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
        Expr expr = unary();

        while (match(TokenType.AND))
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
        Expr expr = literal();

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

    private Expr literal()
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
        Expr expr = assignment();
        if (!match(TokenType.ENDLINE) && !match(TokenType.EOF))
        {
            hadError = true;
            Carmine.error(peek().line + " Invalid expression.");
        }

        return new Stmt.Expression(expr);
    }

    private Stmt varStatement()
    {
        Expr left = expression();

        if (match(TokenType.EQUAL))
        {
            Expr right = expression();
            match(TokenType.ENDLINE);
            if (left instanceof Expr.Variable)
            {

                return new Stmt.Variable(((Expr.Variable)left).getName(), right);
            }

            Carmine.error(peek().line + "Invalid variable.");
            hadError = true;

            return null;
        }

        match(TokenType.ENDLINE);
        if (left instanceof Expr.Variable)
        {
            return new Stmt.Variable(((Expr.Variable)left).getName(), null);
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

    private Stmt statement()
    {
        while (match(TokenType.ENDLINE));
        if (match(TokenType.VARIABLE))
        {
            return varStatement();
        }
        else if (match(TokenType.LBRACE))
        {
            match(TokenType.ENDLINE);
            return new Stmt.Block(blockStatement());
        }
        else if (match(TokenType.DEF))
        {
            return functionStatement();
        }
        return expressionStmt();
    }

    public List<Stmt> parse()
    {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd())
        {
            statements.add(statement());
        }

        return statements;
    }
}
