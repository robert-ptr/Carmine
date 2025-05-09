enum TokenType
{
    LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE,
    ENDLINE,
    COMMA,
    AND, OR,
    NOT,
    EQUAL,
    IDENTIFIER, BINARY, DECIMAL, HEXADECIMAL, TRUE, FALSE,
    DEF, MAIN, ARROW, WIRE, VARIABLE,
    ERR,
    EOF
}

public class Token {
    TokenType type;
    String lexeme;
    Object value;
    int line;

    Token(TokenType type, String lexeme, Object value, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
    }

    public String toString()
    {
        return type + " " + lexeme + " " + value;
    }
}
