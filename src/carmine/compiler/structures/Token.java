package carmine.compiler.structures;

import java.util.Objects;

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

    @Override
    public String toString()
    {
        if (value != null)
            return type + " " + lexeme + " " + value;
        else
            return type + " " + lexeme;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Token other = (Token) obj;
        return type == other.type
                && Objects.equals(lexeme, other.lexeme)
                && Objects.equals(value, other.value)
                && Objects.equals(line, other.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, value, line);
    }

}
