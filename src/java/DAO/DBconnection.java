/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author ASUS
 */
public class DBconnection {
     
    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://HOANGTRINH05;databaseName=ChatbotAI;encrypt=true;trustServerCertificate=true;";
            String username = "Hoang24";
            String password = "123";
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database successfully!");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed to connect to database.");
            e.printStackTrace();
            return null;
        }
    }

    
    public static void printCustomer() {
        String sql = "SELECT * FROM [ChatbotAI].[dbo].[users]";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("Data from Users table:");
            while (rs.next()) {
                System.out.println("Customer ID: " + rs.getInt("user_id"));
                System.out.println("Name: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("Email"));
                System.out.println("Family name: " + rs.getString("family_name"));
                System.out.println("Given name: " + rs.getString("given_name"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve data from Student table: " + e.getMessage());
        }
    }
    
    public static void main(String args[]) {
        printCustomer();
    }
}
