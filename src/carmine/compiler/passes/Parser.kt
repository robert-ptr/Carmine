package carmine.compiler.passes

import carmine.compiler.helpers.CarmineLogger
import carmine.compiler.helpers.LogLevel
import carmine.compiler.structures.*
import java.util.ArrayList

class Parser(private val tokens : ArrayList<Token>)
{
    private var current = 0

    private fun isAtEnd() : Boolean = peek().type == TokenType.EOF

    private fun peek() : Token = tokens[current]

    private fun advance() : Token?
    {
        if (isAtEnd())
            return null

        return tokens[current++]
    }

    private fun previous() : Token
    {
        return tokens[current - 1]
    }

    private fun errorAtCurrent(message : String) {
        Carmine.hadError = true
        CarmineLogger.log(peek(), message, LogLevel.ERROR)

         while (peek().type != TokenType.EOF && peek().type != TokenType.SEMICOLON) {
             advance()
         }

         match(TokenType.SEMICOLON)

         if (Carmine.debugMode)
            Thread.dumpStack()

         throw ParseError()
    }

    private fun check(type : TokenType) : Boolean = !isAtEnd() && peek().type == type

    private fun match(type: TokenType) : Boolean {
        if (peek().type == type) {
            advance()
            return true
        }

        return false
    }

    private fun expression() : Expr? {
        if (match(TokenType.MODULE))
            return moduleExpression()
        else if (match(TokenType.VAR))
            return varExpression()

        return assignment()
    }

    private fun moduleExpression() : Expr? {
        when (val right = assignment()) {
            is Expr.Assignment -> return Expr.Module(right)
            is Expr.Identifier -> return Expr.Module(Expr.Assignment(previous(), null))
            else -> errorAtCurrent("Invalid module declaration.")
        }

        return null
    }

    private fun varExpression() : Expr? {
        when (val right = assignment()) {
            is Expr.Assignment -> return Expr.Variable(right)
            is Expr.Identifier -> return Expr.Variable(Expr.Assignment(previous(), null))
            else -> errorAtCurrent("Invalid variable declaration.")
        }

        return null
    }

    private fun assignment() : Expr? {
        val left = or()

        while (match(TokenType.ASSIGN)) {
            val right = expression()

            if (left is Expr.Identifier) {
                return Expr.Assignment(left.name, right)
            }

            errorAtCurrent("Invalid assignment target.")
        }

        return left
    }

    private fun or() : Expr? {
        var expr = and()

        while (match(TokenType.OR)) {
            val op = previous()
            val right = and()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun and() : Expr? {
        var expr = equality()

        while (match(TokenType.AND)) {
            val op = previous()
            val right = equality()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun equality() : Expr? {
        var expr = comparison()

        while (match(TokenType.EQUAL) || match(TokenType.NOTEQUAL)) {
            val op = previous()
            val right = comparison()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun comparison() : Expr? {
        var expr = term()

        while (match(TokenType.GREATER) || match(TokenType.LESS) || match(TokenType.GREATER_EQUAL) || match(TokenType.LESS_EQUAL)) {
            val op = previous()
            val right = term()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun term() : Expr? {
        var expr = factor()

        while (match(TokenType.MINUS) || match(TokenType.PLUS)) {
            val op = previous()
            val right = factor()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun factor() : Expr? {
        var expr = unary()

        while (match(TokenType.DIV) || match(TokenType.MOD) || match(TokenType.MUL)) {
            val op = previous()
            val right = unary()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun unary() : Expr? {
        if (match(TokenType.NOT) || match(TokenType.MINUS)) {
            val op = previous()
            val right = call()

            return Expr.Unary(op, right)
        }

        return call()
    }

    private fun arguments() : List<Expr?> {
        val args = ArrayList<Expr?>()

        if (!match(TokenType.RPAREN)) {
            do {
                args.add(expression())
            } while (match(TokenType.COMMA))

            if (!match(TokenType.RPAREN))
                errorAtCurrent("Expected ')'.")
        }

        return args
    }

    private fun call() : Expr? {
        val expr = primary()

        if (match(TokenType.LPAREN)) {
            val args = arguments()

            return Expr.Call(peek().line, expr, args)
        }
        else {
            return expr
        }
    }

    private fun primary() : Expr? {
        if (match(TokenType.TRUE))
            return Expr.Literal(peek().line,true)

        if (match(TokenType.FALSE))
            return Expr.Literal(peek().line, false)

        if (match(TokenType.LPAREN)) {
            val expr = expression()
            if (!match(TokenType.RPAREN))
                errorAtCurrent("Expected matching ')' for '('.")

            return Expr.Group(peek().line, expr)
        }

        if (match(TokenType.DECIMAL))
            return Expr.Literal(peek().line, Integer.parseInt(previous().lexeme))

        if (match(TokenType.HEXADECIMAL))
            return Expr.Literal(peek().line, Integer.parseInt(previous().lexeme, 16))

        if (match(TokenType.BINARY))
            return Expr.Literal(peek().line, Integer.parseInt(previous().lexeme, 2))

        if (match(TokenType.NULL))
            return Expr.Literal(peek().line, null)

        if (match(TokenType.IDENTIFIER))
            return Expr.Identifier(previous())

        errorAtCurrent("Unexpected token: " + peek())
        return null
    }

    private fun expressionStmt() : Stmt {
        val expr = expression()

        if (!match(TokenType.SEMICOLON))
           errorAtCurrent("Expected ';' at end of statement.")

        return Stmt.Expression(expr)
    }

    private fun blockStatement() : Stmt {
        val statements = ArrayList<Stmt?>()
        while (!isAtEnd() && !match(TokenType.RBRACE)) {
            try {
                statements.add(statement())
            } catch (_ : ParseError) {
                continue
            }
        }
        //match(TokenType.ENDLINE);
        return Stmt.Block(statements)
    }

    private fun funcStatement(name : Token, isVar : Boolean) : Stmt? {
        val params = ArrayList<Token?>()
        val returnValues = ArrayList<Token?>()
        if (!check(TokenType.RPAREN)) {
            do {
                params.add(advance())
            } while(match(TokenType.COMMA))
        }

        if (!match(TokenType.RPAREN))
            errorAtCurrent("Expected ')'.")

        // then it returns one or multiple values
        if (match(TokenType.ARROW)) {
            do {
                returnValues.add(advance())
            } while(match(TokenType.COMMA))
        }

        if (!match(TokenType.LBRACE))
            errorAtCurrent("Expected '{'.")

        val statements = blockStatement()

        if (isVar && statements is Stmt.Block)
            return Stmt.VarFunction(name, params, returnValues, statements)
        else if(statements is Stmt.Block)
            return Stmt.ModuleFunction(name, params, returnValues, statements)
        else
            errorAtCurrent("Expected block statement.")

        return null
    }

    private fun enumStatement() : Stmt {
        var foundBrace = false
        var name : Token? = null

        if (match(TokenType.IDENTIFIER))
            name = previous()

        if (!match(TokenType.LBRACE))
             errorAtCurrent("Expected '{'.")

        val assignments = ArrayList<Expr.Assignment?>()

        do {
            if (match(TokenType.RBRACE)) {
                foundBrace = true
                break
            }
            val assignment = expression()
            if (assignment !is Expr.Assignment)
                 errorAtCurrent("Invalid assignment.")

            assignments.add(assignment as Expr.Assignment?)
        } while (match(TokenType.COMMA))

        if (!match(TokenType.RBRACE) && !foundBrace)
             errorAtCurrent("Expected '}'.")

        if (!match(TokenType.SEMICOLON))
            errorAtCurrent("Expected ';' at end of statement.")

        return Stmt.Enum(name, assignments)
    }

    private fun ifStatement() : Stmt {
        val condition = expression()

        if (!match(TokenType.LBRACE))
            errorAtCurrent("Expected '{'.")

        val thenBranch = blockStatement()
        if (match(TokenType.ELSE)) {
            var elseBranch : Stmt?
            if (match(TokenType.IF)) {
                elseBranch = ifStatement()
            }
            else {
                if (!match(TokenType.LBRACE))
                    errorAtCurrent("Expected '{'.")
                elseBranch = blockStatement()
            }

            return Stmt.If(condition, thenBranch, elseBranch)
        }

        return Stmt.If(condition, thenBranch, null)
    }

    private fun whileStatement() : Stmt {
        val condition = expression()
        val body = statement()

        return Stmt.While(condition, body)
    }

    // unused right now, still unsure about for syntax
    private fun forStatement() : Stmt {
        if (!match(TokenType.IDENTIFIER))
            errorAtCurrent("Unexpected token: " + peek())

        val varToken = previous()

        if (!match(TokenType.EQUAL))
            errorAtCurrent("Expected assignment.")

        val minValue = expression()

        if (match(TokenType.DOT) && !match(TokenType.DOT)) {
            errorAtCurrent("Missing '..' keyword.")
        }

        val maxValue = expression()

        val body = statement()
        return Stmt.For(varToken, minValue, maxValue, body)
    }

    // global scope
    private fun declaration() : Stmt? {
        if (match(TokenType.MODULE)) {
            if (!match(TokenType.IDENTIFIER))
                errorAtCurrent("Unexpected token in moduleStatement: " + peek())

            val name = previous()

            if (match(TokenType.LPAREN))
                return funcStatement(name, false)
            else {
                current--
                val moduleExpr = Stmt.Expression(moduleExpression())
                if (!match(TokenType.SEMICOLON))
                    errorAtCurrent("Expected ';' at end of module declaration.")

                return moduleExpr
            }
        }
        else if (match(TokenType.VAR)) {
            if (!match(TokenType.IDENTIFIER))
                errorAtCurrent("Unexpected token in varStatement: " + peek())

            val name = previous()

            if (match(TokenType.LPAREN))
                return funcStatement(name, true)
            else {
                current--
                val varExpr = Stmt.Expression(varExpression())
                match(TokenType.SEMICOLON)
                return varExpr
            }
            //{
//
            //}
            //else
            //    return new Stmt.Expression(
            //            new Expr.Variable(
            //                    new Expr.Assignment(name, new Expr.Literal(name.getLine(), null))
            //            )
            //    );
        }
        else if (match(TokenType.ENUM))
            return enumStatement()
        else
            return statement()
    }

    private fun statement() : Stmt {
        if (match(TokenType.LBRACE))
            return blockStatement()
        else if (match(TokenType.IF))
            return ifStatement()
        else if (match(TokenType.WHILE))
            return whileStatement()
        else if (match(TokenType.FOR))
            return forStatement()

        return expressionStmt()
    }

    fun parse() : List<Stmt?> {
        val statements = ArrayList<Stmt?>()
        while (!isAtEnd()) {
            try {
                statements.add(declaration())
            } catch (_ : ParseError) {
                // nothing here for now, just using this so I don't have to add a bunch of
                // return statements when I call errorOnCurrent()
            }
        }

        return statements
    }
}
