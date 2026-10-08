package com.itsc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC practice tasks:
 *  1. Connect, create database StudentsDB and table students
 *  2. Insert one row, then ten more
 *  3. Retrieve and display five rows
 *  4. Update the firstname of a student by id
 *  5. Delete a student by id
 *  6. Calculate the average grade
 */
public class StudentDatabaseOperations {

    private static final String DB_NAME = "StudentsDB";

    public static void main(String[] args) {
        DbConfig cfg = DbConfig.load();

        try {
            // ---------- Task 1: connect, create database, create table ----------
            System.out.println("=== Task 1: Create database and table ===");
            createDatabase(cfg);

            try (Connection connection = DriverManager.getConnection(
                    cfg.databaseUrl(DB_NAME), cfg.user(), cfg.password())) {

                createTable(connection);

                // ---------- Task 2: insert data ----------
                System.out.println("\n=== Task 2: Insert data ===");
                insertSampleData(connection);

                // ---------- Task 3: retrieve five rows ----------
                System.out.println("\n=== Task 3: Retrieve five rows ===");
                retrieveData(connection);

                // ---------- Task 4: update ----------
                System.out.println("\n=== Task 4: Update firstname of student id 1 ===");
                updateStudentName(connection, 1, "UpdatedFirstName");
                retrieveData(connection);

                // ---------- Task 5: delete ----------
                System.out.println("\n=== Task 5: Delete student id 2 ===");
                deleteStudent(connection, 2);
                retrieveData(connection);

                // ---------- Task 6: average grade ----------
                System.out.println("\n=== Task 6: Average grade ===");
                calculateAverageGrade(connection);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Task 1a: create the database (connect to the server, not to a database)
    private static void createDatabase(DbConfig cfg) throws SQLException {
        try (Connection serverConn = DriverManager.getConnection(
                cfg.serverUrl(), cfg.user(), cfg.password());
             Statement statement = serverConn.createStatement()) {

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database '" + DB_NAME + "' is ready.");
        }
    }

    // Task 1b: create the students table
    private static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students ("
                + "id INT PRIMARY KEY, "
                + "firstname VARCHAR(255), "
                + "lastname VARCHAR(255), "
                + "grade INT)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Table 'students' is ready.");
        }
    }

    // Task 2: insert one row, then ten more using a batch
    private static void insertSampleData(Connection connection) throws SQLException {
        // Start from a clean table so the program can be re-run safely
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE students");
        }

        String sql = "INSERT INTO students (id, firstname, lastname, grade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Insert a single row
            ps.setInt(1, 1);
            ps.setString(2, "John");
            ps.setString(3, "Doe");
            ps.setInt(4, 90);
            ps.executeUpdate();
            System.out.println("Inserted 1 row.");

            // Insert ten more rows
            Object[][] rows = {
                {2,  "Aster",  "Nega",     85},
                {3,  "Jemal",  "Edris",    78},
                {4,  "Haile",  "Anaol",    92},
                {5,  "Teddy",  "Habtu",    67},
                {6,  "Teklay", "Michael",  88},
                {7,  "Memar",  "Alebachew",74},
                {8,  "Sara",   "Tesfaye",  95},
                {9,  "Daniel", "Bekele",   81},
                {10, "Hanna",  "Girma",    69},
                {11, "Yonas",  "Kebede",   99}
            };

            for (Object[] row : rows) {
                ps.setInt(1, (Integer) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setString(3, (String) row[2]);
                ps.setInt(4, (Integer) row[3]);
                ps.addBatch();
            }
            int[] counts = ps.executeBatch();
            System.out.println("Inserted " + counts.length + " more rows.");
        }
    }

    // Task 3: retrieve and display five rows
    private static void retrieveData(Connection connection) throws SQLException {
        String sql = "SELECT * FROM students ORDER BY id LIMIT 5";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int grade = rs.getInt("grade");
                System.out.println("ID: " + id + ", Name: " + firstname + " "
                        + lastname + ", Grade: " + grade);
            }
        }
    }

    // Task 4: update the firstname of the student with the given id
    private static void updateStudentName(Connection connection, int id, String newFirstName)
            throws SQLException {
        String sql = "UPDATE students SET firstname = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newFirstName);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();
            System.out.println("Rows updated: " + affected);
        }
    }

    // Task 5: delete the student with the given id
    private static void deleteStudent(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            System.out.println("Rows deleted: " + affected);
        }
    }

    // Task 6: calculate and display the average grade of all students
    private static void calculateAverageGrade(Connection connection) throws SQLException {
        String sql = "SELECT AVG(grade) AS average_grade FROM students";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                double average = rs.getDouble("average_grade");
                if (rs.wasNull()) {
                    System.out.println("No students in the table.");
                } else {
                    System.out.printf("Average Grade: %.2f%n", average);
                }
            }
        }
    }
}
