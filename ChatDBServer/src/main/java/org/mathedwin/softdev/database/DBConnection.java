package org.mathedwin.softdev.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection implements IDBConnection {
    public static final Logger LOGGER = LogManager.getLogger(DBConfig.class);
    private static final DBConfig CONFIG = new DBConfig();

    public Connection getConnection() throws SQLException {
        CONFIG.loadProperties();

        String URL = CONFIG.getURL();
        String USER = CONFIG.getUSER();
        String PASSWORD = CONFIG.getPASSWORD();

        LOGGER.info("Configuration successfully loaded!");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
