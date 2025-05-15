import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Debug {
    public static List<ConstEnvironment> environments = new ArrayList<ConstEnvironment>();

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
