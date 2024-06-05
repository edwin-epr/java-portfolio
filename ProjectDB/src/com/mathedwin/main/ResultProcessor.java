package com.mathedwin.main;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultProcessor<T> {
    void process(T rs) throws SQLException;
}
