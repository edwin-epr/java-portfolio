package com.mathedwin.main;

import java.util.logging.Logger;

public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final DBConnection dbConnection;

    public UserService() {
        dbConnection = new DBConnection();
    }

    public void getAllUser() {
        dbConnection.queryRead("SELECT * FROM users", rs -> {
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String out = String.format("%s %s %s", username, password, email);
                LOGGER.info(out);
            }
        });
    }

    public void getUserById(int id) {
        String sentence = String.format("SELECT * FROM users WHERE id = %s", id);
        dbConnection.queryRead("SELECT * FROM users WHERE id = " + id, rs -> {
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String out = String.format("%s %s %s", username, password, email);
                LOGGER.info(out);
            }
        });
    }

    public void getUserByUsername(String userName) {
        String sentence = String.format("SELECT * FROM users WHERE username = %s", userName);
        dbConnection.queryRead(sentence, rs -> {
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String out = String.format("%s %s %s", username, password, email);
                LOGGER.info(out);
            }
        });
    }

    public void getUserByEmail(String eMail) {
        String sentence = String.format("SELECT * FROM users WHERE email = %s", eMail);
        dbConnection.queryRead(sentence, rs -> {
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String out = String.format("%s %s %s", username, password, email);
                LOGGER.info(out);
            }
        });
    }

    public void createUser(String userName, String pass, String eMail) {
        String sentence = String.format(
                "INSERT INTO users (username, password, email) " +
                        "SELECT * FROM (SELECT '%s' AS username, '%s' AS password, '%s' AS email) AS tmp " +
                        "WHERE NOT EXISTS (SELECT username FROM users WHERE username = '%s') LIMIT 1;",
                userName, pass, eMail, userName
        );
        dbConnection.queryUpdate(sentence, rs -> {
            if (rs > 0) {
                LOGGER.info("Usuario creado exitosamente.");
            } else {
                LOGGER.info("El usuario ya existe.");
            }
        });
    }
}
