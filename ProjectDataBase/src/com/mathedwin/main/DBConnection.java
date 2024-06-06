package com.mathedwin.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    public static void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("database.properties")) {
            props.load(fis);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo properties", e);
        }
    }

    public Connection connect() throws SQLException {
        // Si las propiedades aún no se han cargado, cargarlas
        if (URL == null || USER == null || PASSWORD == null) {
            loadProperties();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
