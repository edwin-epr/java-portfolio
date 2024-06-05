package com.mathedwin.main;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/chat_db";

    private static final String USER = "edwinepr";

    private static final String PASSWORD = "mysqlEdwin@219DataBase#";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void queryRead(String sql, ResultProcessor<ResultSet> processor) {
        try(Connection connection = connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {
            processor.process(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }

    public void queryUpdate(String sql, ResultProcessor<Integer> processor) {
        try(Connection connection = connect();
            Statement statement = connection.createStatement();
        ) {
            Integer resultSet = statement.executeUpdate(sql);
            processor.process(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation error", e);
        }
    }
}
