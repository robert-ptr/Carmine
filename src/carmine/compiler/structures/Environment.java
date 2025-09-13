package carmine.compiler.structures;

import java.util.HashMap;

public interface Environment<T extends Environment<T>> {
    void put(String name, Object value);

    boolean contains(Token token);
    Object get(String name);
    Object get(Token token);

    void addEnclosing(T enclosing);

    T getEnclosing();

    HashMap<String, Object> getVariables();
}
