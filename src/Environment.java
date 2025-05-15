import java.util.HashMap;

public interface Environment {
    void put(String name, Object value);

    boolean contains(Token token);
    Object get(String name);
    Object get(Token token);

    void addEnclosing(ConstEnvironment enclosing);

    ConstEnvironment getEnclosing();

    HashMap<String, Object> getVariables();
}
