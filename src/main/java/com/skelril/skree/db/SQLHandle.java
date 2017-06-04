/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
