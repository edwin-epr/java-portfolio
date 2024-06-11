package com.mathedwin.main.utils;

import com.mathedwin.main.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.isNull;

public class UserUtils {
    public static User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setEmail(resultSet.getString("email"));
        return user;
    }

    public static void validateUser(User user) {

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword()); // <-- Nosotros debemos inventar las reglas para validar la contraseña.

    }


    public static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The ID: " + id + " is not a valid ID.");
        }
    }

    public static void validateUsername(String username) {
        if (isNull(username) || username.isEmpty()) {
            throw new IllegalArgumentException("The username cannot be null or empty.");
        }
    }

    public static void validateEmail(String email) {
        if (isNull(email) || email.isEmpty()) {
            throw new IllegalArgumentException("The email cannot be null or empty.");
        }
    }

    public static void validatePassword(String password) {
        if (isNull(password) || password.isEmpty()) {
            throw new IllegalArgumentException("The password cannot be null or empty.");
        }

        if (password.length() <= 6) {
            throw new IllegalArgumentException("The password must be longer than six characters.");
        }

        if (!password.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("The password can only contain letters and numbers.");
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("The password must contain at least one letter.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("The password must contain at least one number.");
        }
    }
}
