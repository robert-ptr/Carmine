package carmine.compiler.structures;

import java.util.HashMap;
class Environment {
    private var enclosing : Environment? = null
    private val  variables = HashMap<String, Any>()

    fun put(name : String, value : Any)
    {
        variables[name] = value
    }

    fun contains(token : Token) : Boolean
    {
        return variables.containsKey(token.lexeme)
    }

    fun get(name : String) : Any?
    {
        if (variables.containsKey(name))
        {
            return variables[name]
        }

        throw RuntimeException("Unknown variable: $name")
    }

    fun get(token : Token) : Any?
    {
        if (variables.containsKey(token.lexeme))
        {
            return variables[token.lexeme]
        }

        throw RuntimeException("${token.line} Unknown variable: ${token.lexeme}")
    }

    fun addEnclosing(enclosing : Environment)
    {
        this.enclosing = enclosing
    }

    fun getEnclosing() : Environment?
    {
        return enclosing
    }

    fun getVariables() : HashMap<String, Any>
    {
        return variables
    }
}
