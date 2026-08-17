package carmine.compiler.structures;

import java.util.List;

public interface CarmineCallable {
    int arity();
    Object call(List<Object> arguments);
}
