package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/riskdetectiondb";
    private static final String USER = "root";
    private static final String PASSWORD = "JOHNNy90()";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertEvent(String event_timestamp, double latitude, double longitude, double sensorValues, int riskLevel) {
        String query = "INSERT INTO RiskEvents (event_timestamp, latitude, longitude, sensor_value, risk_level) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, event_timestamp);
            statement.setDouble(2, latitude);
            statement.setDouble(3, longitude);
            statement.setDouble(4, sensorValues);
            statement.setInt(5, riskLevel);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

