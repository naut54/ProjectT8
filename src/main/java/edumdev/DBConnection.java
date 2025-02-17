package edumdev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static void connect() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestort8", "root", "");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/gestort8", "root", "");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    public static void disconnect() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestort8", "root", "");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }
}
