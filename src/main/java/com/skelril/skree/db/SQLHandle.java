package com.skelril.skree.db;

import org.jooq.SQLDialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandle {
    private static String database = "";
    private static String username = "";
    private static String password = "";

    public static void setDatabase(String database) {
        SQLHandle.database = database;
    }

    public static void setUsername(String username) {
        SQLHandle.username = username;
    }

    public static void setPassword(String password) {
        SQLHandle.password = password;
    }

    public static SQLDialect getDialect() {
        return SQLDialect.MARIADB;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(database, username, password);
    }
}
