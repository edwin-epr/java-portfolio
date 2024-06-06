package com.mathedwin.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final DBConnection dbConnection;

    public UserService() {
        dbConnection = new DBConnection();
    }

    public void getAllUser() {
        String sql = "SELECT * FROM users";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String out = String.format("%s %s %s", username, password, email);
                LOGGER.info(out);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String out = String.format("%s %s %s", username, password, email);
                    LOGGER.info(out);
                } else {
                    LOGGER.info("No existe el registro");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void getUserByUsername(String userName) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String out = String.format("%s %s %s", username, password, email);
                    LOGGER.info(out);
                } else {
                    LOGGER.info("No existe el registro");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void getUserByEmail(String eMail) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, eMail);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String out = String.format("%s %s %s", username, password, email);
                    LOGGER.info(out);
                } else {
                    LOGGER.info("No existe el registro");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void createUser(String userName, String pass, String eMail) {
        String sql = "INSERT INTO users (username, password, email) " +
                "SELECT * FROM (SELECT ? AS username, ? AS password, ? AS email) AS tmp " +
                "WHERE NOT EXISTS (SELECT username FROM users WHERE username = ?) LIMIT 1;";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            statement.setString(2, pass);
            statement.setString(3, eMail);
            statement.setString(4, userName);
            int result = statement.executeUpdate();
            if (result > 0) {
                LOGGER.info("Usuario creado exitosamente.");
            } else {
                LOGGER.info("El usuario ya existe.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void deleteUserByUsername(String userName) {
        String sql = "DELETE FROM users WHERE username = ?";
        String resetAutoIncrement = "ALTER table users AUTO_INCREMENT = 1;";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            int result = statement.executeUpdate();
            if (result > 0) {
                LOGGER.info("Usuario eliminado exitosamente.");
                try (PreparedStatement resetStatement = connection.prepareStatement(resetAutoIncrement)) {
                    resetStatement.executeUpdate();
                    LOGGER.info("auto increment reset");
                }
            } else {
                LOGGER.info("El usuario no existe.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void updateUserById(int iD, String userName, String passWord, String eMail) {
        String query = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userName);
            statement.setString(2, passWord);
            statement.setString(3, eMail);
            statement.setInt(4, iD);
            int result = statement.executeUpdate();
            if (result > 0) {
                LOGGER.info("Updated user.");
            } else {
                LOGGER.info("The register was not updated.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }
}