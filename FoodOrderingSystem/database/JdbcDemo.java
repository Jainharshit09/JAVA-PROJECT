package database;
import customer.*;
import menu.MenuItem;

import java.sql.*;

public class JdbcDemo {
    private static Connection connection;

    public JdbcDemo() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/FoodOrderingSystem", "root", "ROOT");
            System.out.println("Connection Establish");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertCustomer(Customer customer) {
        try {
            String query = "INSERT INTO Customers (Name, Address, Contact, Premium) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, customer.getName());
                preparedStatement.setString(2, customer.getAddress());
                preparedStatement.setString(3, customer.getContact());
                preparedStatement.setBoolean(4, customer.isPremium());
                preparedStatement.executeUpdate();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Inserting customer failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void insertLoginDetails(String username, String password, int userId) {
        try {
            String query = "INSERT INTO Login (UserId, Username, Password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public static Customer isValidUser(String username, String password) {
        try {
            String query = "SELECT * FROM Login WHERE Username = ? AND Password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // If the user is valid, retrieve their details from the 'Customers' table
                        int userId = resultSet.getInt("UserId");
                        return getCustomerById(userId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
     public static Customer isValidUser(String username) {
        try {
            String query = "SELECT * FROM Login WHERE Username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("UserId");
                        return getCustomerById(userId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Customer getCustomerById(int customerId) {
        try {
            String query = "SELECT * FROM Customers WHERE UserId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("Name");
                        String address = resultSet.getString("Address");
                        String contact = resultSet.getString("Contact");
                        boolean isPremium = resultSet.getBoolean("Premium");
                        return new Customer(name, address, contact) {
                            @Override
                            public boolean isPremium() {
                                return isPremium;
                            }
                        };
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    public static boolean insertReviewAndRating(String username, String review, float rating) {
        try {
            int userId = getUserIdByUsername(username);
            if (userId != -1) {
                String query = "INSERT INTO Reviews (UserId, Review, Rating) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setString(2, review);
                    preparedStatement.setFloat(3, rating);
                    preparedStatement.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static int getUserIdByUsername(String username) {
        try {
            String query = "SELECT UserId FROM Login WHERE Username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("UserId");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static  int  insertOrder(String username, double totalAmount) {
        try {
            int userId = getUserIdByUsername(username);
            if (userId != -1) {
                String query = "INSERT INTO Orders (UserId, TotalAmount) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setDouble(2, totalAmount);
                    preparedStatement.executeUpdate();

                    // Get the auto-generated orderId
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Inserting order failed, no ID obtained.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public void insertMenuItem(MenuItem menuItem) {
        try {
            String query = "INSERT INTO MenuItems (Name, Price) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, menuItem.getName());
                preparedStatement.setDouble(2, menuItem.getPrice());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void retrieveMenuItems() {
        try {
            String query = "SELECT * FROM MenuItems";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
    
                System.out.println("Menu:");
                while (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    double price = resultSet.getDouble("Price");
                    System.out.println(name + " - " + price);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    
    

    
