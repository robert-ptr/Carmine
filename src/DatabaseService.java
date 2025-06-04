import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
                        "CREATE TABLE IF NOT EXISTS " + SCHEMA + "." + TABLE + " (" +
                                "id TEXT PRIMARY KEY, " +
                                "specification TEXT NOT NULL" +
                                ")";
                stmt.execute(createTableSQL);

                // Set the search path
                stmt.execute("SET search_path TO " + SCHEMA + ", public");

                AuditService.logAction("Database schema and table initialized successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }

    private boolean isValidCircuitName(String name) {
        // Add your circuit name validation rules here
        return name != null &&
                name.matches("^[A-Za-z0-9_]+$") && // Only alphanumeric, underscore
                name.length() >= 2 &&
                name.length() <= 50;
    }

    private boolean circuitNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM " + SCHEMA + "." + TABLE + " WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void writeCircuit(String circuitName, String specification) {
        // Validate circuit name
        if (!isValidCircuitName(circuitName)) {
            System.err.println("Invalid circuit name. Use only alphanumeric characters, underscore (2-50 characters).");
            return;
        }

        // Check if circuit name already exists
        if (circuitNameExists(circuitName)) {
            System.err.println("A circuit with name '" + circuitName + "' already exists.");
            return;
        }

        String sql = "INSERT INTO " + SCHEMA + "." + TABLE +
                " (id, specification) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, circuitName);
            pstmt.setString(2, specification);

            pstmt.executeUpdate();
            AuditService.logAction("Circuit '" + circuitName + "' written successfully!");

        } catch (SQLException e) {
            System.err.println("Error creating circuit!");
            e.printStackTrace();
        }
    }

    // Read (R)
    public String readCircuit(String id) {
        String sql = "SELECT * FROM " + SCHEMA + "." + TABLE + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AuditService.logAction("Successfully read circuit '" + id + "'");
                    return rs.getString("specification");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading circuit with ID: " + id);
            e.printStackTrace();
        }

        return null;
    }

    // Update (U)
    public boolean updateCircuit(String id, String newSpecification) {
        // Validate new circuit name
        if (!isValidCircuitName(id)) {
            System.err.println("Invalid circuit name. Use only alphanumeric characters, underscore (2-50 characters).");
            return false;
        }

        // Check if new name already exists (except for current circuit)
        String checkSql = "SELECT COUNT(*) FROM " + SCHEMA + "." + TABLE + " WHERE specification = ? AND id != ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, newSpecification);
            checkStmt.setString(2, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("A circuit with name '" + id + "' already exists.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE " + SCHEMA + ".circuits SET specification = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newSpecification);
            pstmt.setString(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                AuditService.logAction("Specification updated successfully: " + newSpecification);
                return true;
            } else {
                System.out.println("No circuit found with ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating circuit!");
            e.printStackTrace();
            return false;
        }
    }

    // Delete (D)
    public boolean deleteCircuit(String id) {
        String sql = "DELETE FROM " + SCHEMA + "." + TABLE + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                AuditService.logAction("Circuit deleted successfully: " + id);
                return true;
            } else {
                System.out.println("No circuit found with ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting circuit!");
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllCircuitsSorted() {
        List<Circuit> circuits = new ArrayList<>();
        String sql = "SELECT * FROM " + SCHEMA + "." + TABLE;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String specStr = rs.getString("specification");
                circuits.add(new Circuit(id, specStr));
            }

            Collections.sort(circuits, (c1, c2) -> c1.getId().compareToIgnoreCase(c2.getId()));
            AuditService.logAction("Successfully retrieved and sorted " + circuits.size() + " circuits");

            return circuits.stream()
                    .map(Circuit::getSpecification)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            System.err.println("Error reading all circuits!");
            e.printStackTrace();
        }

        return new ArrayList<>();
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

    private static class Circuit {
        private final String id;
        private final String specification;

        public Circuit(String id, String specification) {
            this.id = id;
            this.specification = specification;
        }

        public String getId() {
            return id;
        }

        public String getSpecification() {
            return specification;
        }
    }
}