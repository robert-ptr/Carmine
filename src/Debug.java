import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Debug {
    public static List<Environment> environments = new ArrayList<Environment>();

    public static void printEnvironments()
    {
        environments.add(Carmine.environment);

        for (Environment environment : environments)
        {
            System.out.println("environment");
            HashMap<String, Object> variables = environment.getVariables();

            for (String key : variables.keySet())
            {
                System.out.println(key + ": " + variables.get(key));
            }
        }
    }
}
