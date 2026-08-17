package carmine.compiler.structures;

import java.util.Objects;

enum class TokenType
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
    ENUM,
    ARROW, //WIRE, INPUT,
    MODULE, VAR,
    ERR,
    EOF
}

class Token (val type: TokenType,
             val lexeme: String,
             var tokenValue: Any?,
             val line: Int
){
    override fun toString() : String
    {
        return if (tokenValue != null)
            "$type $lexeme $tokenValue"
        else
            "$type $lexeme"
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other)
            return true

        if (other is Token)
            return type == other.type
                    && Objects.equals(lexeme, other.lexeme)
                    && Objects.equals(tokenValue, other.tokenValue)
                    && Objects.equals(line, other.line)

        return false
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + type.hashCode()
        result = 31 * result + lexeme.hashCode()
        result = 31 * result + tokenValue.hashCode()
        return result
    }

}
