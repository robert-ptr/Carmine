import java.util.HashMap;

public class ModuleEnvironment implements Environment<ModuleEnvironment> {
    private ModuleEnvironment enclosing = null;
    private HashMap<String, Object> variables = new HashMap<>();

    public void put(String name, Object value)
    {
        variables.put(name, value);
    }

    public boolean contains(Token token)
    {
        return variables.containsKey(token.lexeme);
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
        if (variables.containsKey(token.lexeme))
        {
            return variables.get(token.lexeme);
        }

        throw new RuntimeException(token.line + " Unknown variable: " + token.lexeme);
    }

    public void addEnclosing(ModuleEnvironment enclosing)
    {
        this.enclosing = enclosing;
    }

    public ModuleEnvironment getEnclosing()
    {
        return enclosing;
    }

    public HashMap<String, Object> getVariables()
    {
        return variables;
    }
}
