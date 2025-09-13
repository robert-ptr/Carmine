package carmine.compiler.structures;

public class Token {
    private TokenType type;
    private String lexeme;
    private Object value;
    private int line;

    public Token(TokenType type, String lexeme, Object value, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
    }

    public TokenType getType()
    {
        return type;
    }

    public String getLexeme()
    {
        return lexeme;
    }

    public Object getValue()
    {
        return value;
    }

    public int getLine() {
        return line;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String toString()
    {
        return type + " " + lexeme + " " + value;
    }
}
