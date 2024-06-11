package com.mathedwin.main.database;

import java.sql.*;
import java.util.logging.Logger;

public class DBConnection implements IDBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final DBConfig CONFIG = new DBConfig();


    public Connection getConnection() throws SQLException {
        CONFIG.loadProperties();
        LOGGER.info("Configuration successfully loaded!");

        String URL = CONFIG.getURL();
        String USER = CONFIG.getUSER();
        String PASSWORD = CONFIG.getPASSWORD();

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
