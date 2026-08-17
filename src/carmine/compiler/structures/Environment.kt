package carmine.compiler.structures;

import java.util.HashMap;

public class Environment {
    private Environment enclosing = null;
    private HashMap<String, Object> variables = new HashMap<>();

    public void put(String name, Object value)
    {
        variables.put(name, value);
    }

    public boolean contains(Token token)
    {
        return variables.containsKey(token.getLexeme());
    }

    public Object get(String name)
    {
        if (variables.containsKey(name))
        {
            return variables.get(name);
        }

        throw new RuntimeException("Unknown variable: " + name);
    }

    public Object get(Token token)
    {
        if (variables.containsKey(token.getLexeme()))
        {
            return variables.get(token.getLexeme());
        }

        throw new RuntimeException(token.getLine() + " Unknown variable: " + token.getLexeme());
    }

    public void addEnclosing(Environment enclosing)
    {
        this.enclosing = enclosing;
    }

    public Environment getEnclosing()
    {
        return enclosing;
    }

    public HashMap<String, Object> getVariables()
    {
        return variables;
    }
}
