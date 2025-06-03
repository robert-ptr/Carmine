import java.sql.*;


public class DatabaseService {
    // Database connection parameters
    private String URL = "jdbc:postgresql://localhost:5432/postgres";
    private String USER = "postgres";
    private String PASSWORD = "your_new_password";
    private String SCHEMA = "java";
    private String TABLE = "circuits";
    private AuditService audit;


    private static DatabaseService instance;
    private static Connection connection;

    private DatabaseService(String URL, String USER, String PASSWORD, String SCHEMA, String TABLE)
    {
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.SCHEMA = SCHEMA;
        this.TABLE = TABLE;

        this.audit = AuditService.getInstance();
    }

    public static DatabaseService getInstance(String URL, String USER, String PASSWORD, String SCHEMA, String TABLE)
    {
        if (instance == null)
            instance = new DatabaseService(URL, USER, PASSWORD, SCHEMA, TABLE);

        return instance;
    }

    public void connect()
    {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Create schema and table if they don't exist
            try (Statement stmt = connection.createStatement()) {
                // Create schema if it doesn't exist
                stmt.execute("CREATE SCHEMA IF NOT EXISTS " + SCHEMA);

                // Create table if it doesn't exist
                String createTableSQL =
                        "CREATE TABLE IF NOT EXISTS " + SCHEMA + ".circuits (" +
                                "id SERIAL PRIMARY KEY, " +
                                "specification TEXT NOT NULL" +
                                ")";
                stmt.execute(createTableSQL);

                // Set the search path
                stmt.execute("SET search_path TO " + SCHEMA + ", public");

                System.out.println("Database schema and table initialized successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }

    public void writeString(String specification) {
        String sql = "INSERT INTO " + SCHEMA + "." + TABLE + " (specification) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, specification);
            pstmt.executeUpdate();
            System.out.println("String successfully written to database!");
        } catch (SQLException e) {
            System.err.println("Error writing to database!");
            e.printStackTrace();
        }
    }
    public String readString(long id) {
        String sql = "SELECT * FROM " + SCHEMA + "." + TABLE + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("specification");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading from database!");
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection!");
            e.printStackTrace();
        }
    }
}