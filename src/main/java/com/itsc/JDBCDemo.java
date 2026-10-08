package com.itsc;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Lab step 1-3: register the driver and establish a connection to MySQL.
 * Run this first to verify your setup works.
 */
public class JDBCDemo {

    public static void main(String[] args) {
        DbConfig cfg = DbConfig.load();

        try {
            // Optional since JDBC 4.0 (Java 6+): the driver registers itself
            // when the JAR is on the classpath.
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    cfg.serverUrl(), cfg.user(), cfg.password())) {
                System.out.println("Established Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
