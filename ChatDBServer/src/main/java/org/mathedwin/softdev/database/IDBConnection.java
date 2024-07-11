package org.mathedwin.softdev.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDBConnection {
    Connection getConnection() throws SQLException;
}
