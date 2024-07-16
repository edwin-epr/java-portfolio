package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.database.DBConnection;
import org.mathedwin.softdev.exceptions.DatabaseException;
import org.mathedwin.softdev.model.User;
import org.mathedwin.softdev.repository.IUserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mathedwin.softdev.database.queries.UserQueries.*;
import static org.mathedwin.softdev.utils.UserUtils.*;

public class UserService implements IUserService {

    public static final Logger LOGGER = LogManager.getLogger(UserService.class);
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
        } catch (SQLException exception) {
            LOGGER.error("Error retrieving users", exception);
            throw new DatabaseException("Error retrieving users", exception);
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
        } catch (SQLException exception) {
            String messageError = String.format("Error retrieving user with ID: %d", id);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
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
        } catch (SQLException exception) {
            String messageError = String.format("Error retrieving user with username: %s", username);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
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
        } catch (SQLException exception) {
            String messageError = String.format("Error retrieving user with email: %s", email);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
        }
    }

    public void saveUser(User user) {
        try {
            validateUser(user);
        } catch (IllegalArgumentException exception) {
            String messageError = String.format("Error validating user: %s", user);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
        }

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
        } catch (SQLException exception) {
            String messageError = String.format("Error saving user: %s", user);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
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
            LOGGER.error("Database operation error", e);
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
            LOGGER.error("Database operation error", e);
        }
    }
}
