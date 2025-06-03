import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditService {
    private static AuditService instance;
    private static final String FILE_NAME = "audit.csv";

    private AuditService() {}

    public static AuditService getInstance()
    {
        if (instance == null)
            instance = new AuditService();

        return instance;
    }

    public void logAction(String action)
    {
        try (FileWriter writer = new FileWriter(FILE_NAME))
        {
            writer.append(action).append(",").append(LocalDateTime.now().toString()).append("\n");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
