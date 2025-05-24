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
        // keywords.put("and", TokenType.AND); //
        // keywords.put("or", TokenType.OR);   //
        // keywords.put("not", TokenType.NOT); //
        keywords.put("wire", TokenType.WIRE);
        keywords.put("def", TokenType.DEF);
        keywords.put("main", TokenType.MAIN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("module", TokenType.MODULE);
        keywords.put("const", TokenType.CONST);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("enum", TokenType.ENUM);
        keywords.put("null", TokenType.NULL);
        keywords.put("input", TokenType.INPUT);
        keywords.put("enum", TokenType.ENUM);
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

    private boolean isHexadecimal(char c) { return isDecimal(c) || (c >= 'a') && (c <= 'f') || (c >= 'A') && (c <= 'F'); }

    private boolean isBinary(char c) { return c == '0' || c == '1'; }

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

    private Token binary()
    {
        while (isBinary(peek()))
        {
            advance();
        }

        return makeToken(TokenType.BINARY);
    }

    private Token hexadecimal()
    {
        while (isHexadecimal(peek()))
        {
            advance();
        }

        return makeToken(TokenType.HEXADECIMAL);
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
                if (peek() == '=')
                    return makeToken(TokenType.EQUAL);
                return makeToken(TokenType.ASSIGN);
            case '<':
                if (peek() == '=')
                    return makeToken(TokenType.LESS_EQUAL);
                return makeToken(TokenType.LESS);
            case '>':
                if (peek() == '=')
                    return makeToken(TokenType.GREATER_EQUAL);
                return makeToken(TokenType.GREATER);
            case '&':
                return makeToken(TokenType.AND);
            case '|':
                return makeToken(TokenType.OR);
            case '!':
                if (peek() == '=')
                    return makeToken(TokenType.NOTEQUAL);
                //return makeToken(TokenType.NOT);
                Carmine.error("Unknown literal: '" + c + "' at line " + line) ;
                return makeToken(TokenType.ERR);
            case '+':
                return makeToken(TokenType.PLUS);
            case '-':
                if (peek() == '>')
                    return makeToken(TokenType.ARROW);
                return makeToken(TokenType.MINUS);
            case '/':
                if (advance() == '/')
                {
                    while (!isAtEnd() && peek() != '\n')
                        advance();

                    if (peek() == '\n')
                        advance();
                }
            case '%':
                return makeToken(TokenType.MOD);
            case '*':
                if (peek() == '*')
                    return makeToken(TokenType.EXP);
                return makeToken(TokenType.MUL);
            //case ';':
            //    return makeToken(TokenType.SEMICOLON);
            case '.':
                return makeToken(TokenType.DOT);
            case ' ':
            case '\r':
            case '\t':
                return null;
            case '0':
                if (peek() == 'b')
                {
                    return binary();
                }
                else if (peek() == 'X')
                {
                    return hexadecimal();
                }
                else
                {
                    return number();
                }
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
                    return makeToken(TokenType.ERR);
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
