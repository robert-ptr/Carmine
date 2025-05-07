import java.util.HashMap;

public class Environment
{
    private Environment enclosing = null;
    private HashMap<String, Object> variables = new HashMap<>();

    void put(String name, Object value)
    {
        variables.put(name, value);
    }

    boolean contains(Token token)
    {
        return variables.containsKey(token.lexeme);
    }

    Object get(String name)
    {
        if (variables.containsKey(name))
        {
            return variables.get(name);
        }

        throw new RuntimeException("Unknown variable: " + name);
    }

    Object get(Token token)
    {
        if (variables.containsKey(token.lexeme))
        {
            return variables.get(token.lexeme);
        }

        throw new RuntimeException(token.line + " Unknown variable: " + token.lexeme);
    }

    void addEnclosing(Environment enclosing)
    {
        this.enclosing = enclosing;
    }

    Environment getEnclosing()
    {
        return enclosing;
    }

    HashMap<String, Object> getVariables()
    {
        return variables;
    }
}
