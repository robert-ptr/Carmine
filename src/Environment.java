import java.util.HashMap;

public class Environment
{
    HashMap<String, Object> variables = new HashMap<>();

    void put(String name, Object value)
    {
        variables.put(name, value);
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
}
