import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingPermission;

public class Parser
{
    List<Token> tokens = new ArrayList<Token>();
    int current = 0;

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
        return tokens.get(current++);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private boolean check(TokenType type)
    {
        if (peek().type == TokenType.EOF)
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

        return literal();
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

        Carmine.error(peek().line + "Unexpected token.");
        return null;
    }

    private Stmt expressionStmt()
    {
        Expr expr = expression();

        return new Stmt.Expression(expr);
    }

    private Stmt statement()
    {
        return expressionStmt();
    }

    public List<Stmt> parse()
    {
        List<Stmt> statements = new ArrayList<>();
        while (peek().type != TokenType.EOF)
        {
            statements.add(statement());
        }

        return statements;
    }
}
