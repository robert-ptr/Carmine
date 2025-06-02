import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Debug {
    public static List<ConstEnvironment> environments = new ArrayList<ConstEnvironment>();

    private int nodeId = 0;
    private StringBuilder builder = new StringBuilder();

    public String generateDot(Expr expr)
    {
        builder.append("digraph AST {\n");
        expr.dotWalk(builder, -1);
        builder.append("}\n");
        return builder.toString();
    }

    public String generateDot(Stmt stmt)
    {
        builder.append("digraph AST {\n");
        stmt.dotWalk(stmt, -1);
        builder.append("}\n");
        return builder.toString();
    }

    public static void printEnvironments()
    {
        environments.add(Carmine.constEnvironment);

        for (ConstEnvironment constEnvironment : environments)
        {
            System.out.println("environment");
            HashMap<String, Object> variables = constEnvironment.getVariables();

            for (String key : variables.keySet())
            {
                System.out.println(key + ": " + variables.get(key));
            }
        }
    }
}
