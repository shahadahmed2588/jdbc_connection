# JDBC Lab - Student Database Operations

Java + JDBC + MySQL lab (Addis Ababa University). Covers connecting to MySQL,
creating a database/table, and running INSERT, SELECT, UPDATE, DELETE and
aggregate (AVG) queries.

## Requirements
- JDK 17+
- MySQL Server running on your machine
- Maven (or Eclipse with its built-in Maven support)

## Setup
1. Copy `db.properties.example` to `db.properties`.
2. Put your own MySQL username/password in `db.properties`
   (this file is git-ignored, so your password is never pushed).

## Run
```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.itsc.JDBCDemo                  # test the connection
mvn exec:java -Dexec.mainClass=com.itsc.StudentDatabaseOperations # run Tasks 1-6
```
Or in Eclipse: *File > Import > Existing Maven Projects*, then right-click a
class > *Run As > Java Application* (run from the project root so
`db.properties` is found).

## Tasks implemented (`StudentDatabaseOperations.java`)
| Task | What it does |
|------|--------------|
| 1 | Create database `StudentsDB` and table `students (id, firstname, lastname, grade)` |
| 2 | Insert one row, then ten more (PreparedStatement + batch) |
| 3 | Retrieve and display five rows |
| 4 | Update a student's `firstname` by `id` |
| 5 | Delete a student by `id` |
| 6 | Calculate and display the average grade |
