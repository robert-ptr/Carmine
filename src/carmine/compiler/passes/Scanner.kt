package carmine.compiler.passes;

import carmine.compiler.structures.Token;
import carmine.compiler.structures.TokenType;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.helpers.LogLevel;

import java.util.ArrayList;
import java.util.HashMap;

class Scanner(private val code: String) {
    var line = 1
    var start = 0
    var current = 0

    val keywords:HashMap<String, TokenType> = hashMapOf(
        "and" to TokenType.AND,
        "or" to TokenType.OR,
        "not" to TokenType.NOT,
        //"wire" to TokenType.WIRE,
        //"def" to TokenType.DEF,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "module" to TokenType.MODULE,
        "var" to TokenType.VAR,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "for" to TokenType.FOR,
        "while" to TokenType.WHILE,
        "enum" to TokenType.ENUM,
        "null" to TokenType.NULL,
        //"input" to TokenType.INPUT
    )

    fun peek() : Char = if (!isAtEnd()) code[current] else '\u0000'

    fun advance() : Char = if(!isAtEnd()) code[current++] else '\u0000'

    fun isAtEnd() : Boolean = current >= code.length

    fun makeToken(type : TokenType) : Token = Token(type, code.substring(start, current), null, line)

    fun isAlpha(c : Char) : Boolean = (c in 'A'..'Z') || (c in 'a'..'z') || c == '_'

    fun isDecimal(c : Char) : Boolean = c in '0'..'9'

    fun isHexadecimal(c : Char) : Boolean =
        isDecimal(c) || (c >= 'a') && (c <= 'f') || (c >= 'A') && (c <= 'F')

    fun isBinary(c : Char) : Boolean = c == '0' || c == '1'

    fun identifier() : Token
    {
        while (isAlpha(peek()) || isDecimal(peek()))
        {
            advance()
        }

        if (keywords.containsKey(code.substring(start, current)))
        {
            return makeToken(keywords[code.substring(start, current)]!!)
        }

        return makeToken(TokenType.IDENTIFIER)
    }

    fun number() : Token
    {
        while (isDecimal(peek()))
        {
            advance()
        }
        val number = makeToken(TokenType.DECIMAL)
        number.value = Integer.parseInt(number.lexeme)
        return number
    }

    fun binary() : Token
    {
        while (isBinary(peek()))
        {
            advance()
        }

        return makeToken(TokenType.BINARY)
    }

    fun hexadecimal() : Token
    {
        while (isHexadecimal(peek()))
        {
            advance()
        }

        return makeToken(TokenType.HEXADECIMAL)
    }

    fun scanToken() : Token?
    {
        start = current;
        return when(val c = advance()) {
            ',' -> makeToken(TokenType.COMMA)
            ';' -> makeToken(TokenType.SEMICOLON)
            '(' -> makeToken(TokenType.LPAREN)
            ')' -> makeToken(TokenType.RPAREN)
            '{' -> makeToken(TokenType.LBRACE)
            '}' -> makeToken(TokenType.RBRACE)
            '[' -> makeToken(TokenType.LBRACKET)
            ']' -> makeToken(TokenType.RBRACKET)
            '\n' -> {
                //makeToken(TokenType.ENDLINE);
                line++;
                return null
            }
            '=' ->
                if (peek() == '=') {
                    advance();
                    makeToken(TokenType.EQUAL)
                }
                else
                    makeToken(TokenType.ASSIGN)
            '<' ->
                if (peek() == '=') {
                    advance()
                    makeToken(TokenType.LESS_EQUAL)
                }
                else
                    makeToken(TokenType.LESS)
            '>' ->
                if (peek() == '=') {
                    advance()
                    makeToken(TokenType.GREATER_EQUAL)
                }
                else
                    makeToken(TokenType.GREATER)
            '&' -> makeToken(TokenType.AND)
            '|' -> makeToken(TokenType.OR)
            '!' ->
                if (peek() == '=') {
                    advance()
                    makeToken(TokenType.NOTEQUAL)
                }
                else {
                    //return makeToken(TokenType.NOT)
                    CarmineLogger.log("Unknown literal: '$c' at line $line", LogLevel.ERROR)
                    return makeToken(TokenType.ERR)
                }
            '+' -> makeToken(TokenType.PLUS)
            '-' ->
                if (peek() == '>') {
                    advance()
                    makeToken(TokenType.ARROW)
                }
                else
                    makeToken(TokenType.MINUS)
            '/' ->
                if (peek() == '/')
                {
                    advance()
                    while (!isAtEnd() && peek() != '\n')
                        advance()

                    if (peek() == '\n')
                        advance()

                    return null
                }
                else
                    makeToken(TokenType.DIV)
            '%' -> makeToken(TokenType.MOD)
            '*' ->
                if (peek() == '*') {
                    advance()
                    makeToken(TokenType.EXP)
                }
                else
                    makeToken(TokenType.MUL)
            //';' -> makeToken(TokenType.SEMICOLON)
            '.' -> makeToken(TokenType.DOT)
            ' ', '\r', '\t' -> return null;
            '0' ->
                if (peek() == 'b')
                {
                    advance()
                    binary()
                }
                else if (peek() == 'X')
                {
                    advance()
                    hexadecimal()
                }
                else
                    number()
            else ->
                if (isAlpha(c))
                    identifier()
                else if (isDecimal(c))
                    number()
                else
                {
                    CarmineLogger.log("Unknown literal: '$c' at line $line", LogLevel.ERROR)
                    makeToken(TokenType.ERR)
                }
        }
    }

    fun scanTokens() : MutableList<Token>
    {
        val tokens = ArrayList<Token>()
        while (!isAtEnd())
        {
            val token = scanToken()
            if (token != null)
                tokens.add(token)
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }
}
