import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scanner {
    private int line = 1;
    private int start = 0;
    private int current = 0;
    private final String code;

    private static HashMap<String, TokenType> keywords;

    static
    {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("or", TokenType.OR);
        keywords.put("not", TokenType.NOT);
        keywords.put("wire", TokenType.WIRE);
        keywords.put("def", TokenType.DEF);
        keywords.put("main", TokenType.MAIN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("var", TokenType.VARIABLE);
    }

    private char peek()
    {
        if (!isAtEnd())
            return code.charAt(current);

        return '\0';
    }

    private char advance()
    {
        if (!isAtEnd())
            return code.charAt(current++);

        return '\0';
    }

    private boolean isAtEnd()
    {
        return current >= code.length();
    }

    private Token makeToken(TokenType type)
    {
        return new Token(type, code.substring(start, current), null, line);
    }

    private boolean isAlpha(char c)
    {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    private boolean isDecimal(char c)
    {
        return c >= '0' && c <= '9';
    }

    private Token identifier()
    {
        while (isAlpha(peek()) || isDecimal(peek()))
        {
            advance();
        }

        if (keywords.containsKey(code.substring(start, current)))
        {
            return makeToken(keywords.get(code.substring(start, current)));
        }

        return makeToken(TokenType.IDENTIFIER);
    }

    private Token number()
    {
        while (isDecimal(peek()))
        {
            advance();
        }
        Token number = makeToken(TokenType.DECIMAL);
        number.value = Integer.parseInt(number.lexeme);
        return number;
    }

    private Token scanToken()
    {
        start = current;
        char c = advance();
        switch(c)
        {
            case ',':
                return makeToken(TokenType.COMMA);
            case '(':
                return makeToken(TokenType.LPAREN);
            case ')':
                return makeToken(TokenType.RPAREN);
            case '{':
                return makeToken(TokenType.LBRACE);
            case '}':
                return makeToken(TokenType.RBRACE);
            case '[':
                return makeToken(TokenType.LBRACKET);
            case ']':
                return makeToken(TokenType.RBRACKET);
            case '\n':
                Token token = makeToken(TokenType.ENDLINE);
                line++;
                return token;
            case '=':
                return makeToken(TokenType.EQUAL);
            case '-':
                if (advance() == '>')
                    return makeToken(TokenType.ARROW);
                else // no substraction for now
                {
                    Carmine.error(line + "Subtraction is not a supported operation.");
                    return null;
                }
            case '/':
                if (advance() == '/')
                {
                    while (!isAtEnd() && peek() != '\n')
                        advance();

                    if (peek() == '\n')
                        advance();
                }
                else // no division for now
                {
                    Carmine.error(line + " Division is not a supported operation.");
                    return null;
                }
            case ' ':
            case '\r':
            case '\t':
                return null;
            default:
                if (isAlpha(c))
                {
                    return identifier();
                }
                else if (isDecimal(c))
                {
                    return number();
                }
                else
                {
                    Carmine.error("Unknown literal: '" + c + "' at line " + line) ;
                    return null;
                }
        }
    }

    public List<Token> scanTokens()
    {
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd())
        {
            Token token = scanToken();
            if (token != null)
                tokens.add(token);
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    Scanner(String code)
    {
        this.code = code;
    }
}
