enum TokenType
{
    LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE,
    ENDLINE,
    COMMA, SEMICOLON, DOT,
    PLUS, MINUS,
    MUL, DIV, MOD, EXP,
    AND, OR, NOT,
    EQUAL, NOTEQUAL, LESS, GREATER, GREATER_EQUAL, LESS_EQUAL,
    ASSIGN,
    IF, ELSE, WHILE, FOR,
    IDENTIFIER, BINARY, DECIMAL, HEXADECIMAL, TRUE, FALSE, NULL,
    DEF, MAIN, ENUM,
    ARROW, //WIRE, INPUT,
    MODULE, CONST,
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
