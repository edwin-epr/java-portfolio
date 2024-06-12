package com.mathedwin.main.service;

import com.mathedwin.main.database.DBConnection;
import com.mathedwin.main.exceptions.DatabaseException;
import com.mathedwin.main.model.User;
import com.mathedwin.main.repository.IUserService;

import static com.mathedwin.main.database.queries.UserQueries.*;
import static com.mathedwin.main.utils.UserUtils.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService implements IUserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final DBConnection dbConnection;

    public UserService() {
        dbConnection = new DBConnection();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_USERS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving users", e);
            throw new DatabaseException("Error retrieving users", e);
        }
        return Collections.unmodifiableList(users);
    }

    @Override
    public Optional<User> getUserById(int id) {

        validateId(id);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID_QUERY)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user with ID: " + id, e);
            throw new DatabaseException("Error retrieving user with ID: " + id, e);
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) {

        validateUsername(username);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_USERNAME_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user with username: " + username, e);
            throw new DatabaseException("Error retrieving user with username: " + username, e);
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

        validateEmail(email);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_EMAIL_QUERY)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user with email: " + email, e);
            throw new DatabaseException("Error retrieving user with email: " + email, e);
        }
    }

    public void saveUser(User user) {

        validateUser(user);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_USER_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new DatabaseException("Error when creating the user, no ID was obtained or already exists.");
                }
                user.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: ", e);
            throw new DatabaseException("Error saving user: " + user, e);
        }
    }

    // TODO: implementar los métodos faltantes

    public void deleteUserByUsername(String userName) {
        String sql = "DELETE FROM users WHERE username = ?";
        String resetAutoIncrement = "ALTER table users AUTO_INCREMENT = 1;";
        try (Connection connection = dbConnection.getConnection();
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
        try (Connection connection = dbConnection.getConnection();
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