import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService { // writes the netlist to a database
    private static final String URL = "jdbc:sqlite:database.db";
    private static Connection connection = null;

    private DatabaseService() {}

    public static Connection getConnection()
    {
        if (connection == null)
        {
            try
            {
                connection = DriverManager.getConnection(URL);
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return connection;
    }
}
