package com.scc.dao;

import com.scc.model.User;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void init() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            String createUsers = """
                CREATE TABLE IF NOT EXISTS Users (
                    User_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Username TEXT NOT NULL UNIQUE,
                    Password TEXT NOT NULL,
                    Role TEXT NOT NULL CHECK(Role IN ('ADMIN', 'LIBRARIAN', 'STUDENT')),
                    AccountStatus TEXT NOT NULL DEFAULT 'PENDING'
                    CHECK(AccountStatus IN ('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED'))
                )
                """;
            stmt.execute(createUsers);

            String createMembers = """
                CREATE TABLE IF NOT EXISTS Members (
                    Member_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Member_Name TEXT NOT NULL,
                    ContactNumber TEXT,
                    Address TEXT,
                    Status TEXT DEFAULT 'ACTIVE',
                    User_ID INTEGER,
                    FOREIGN KEY (User_ID) REFERENCES Users(User_ID) ON DELETE SET NULL
                )
                """;
            stmt.execute(createMembers);

            String createBooks = """
                CREATE TABLE IF NOT EXISTS Books (
                    Book_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Book_Title TEXT NOT NULL,
                    Author TEXT,
                    Category TEXT,
                    Quantity INTEGER DEFAULT 1,
                    Availability TEXT DEFAULT 'AVAILABLE',
                    Status TEXT DEFAULT 'ACTIVE'
                )
                """;
            stmt.execute(createBooks);

            String createBorrowing = """
                CREATE TABLE IF NOT EXISTS Borrowing (
                    Borrowing_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Book_ID INTEGER NOT NULL,
                    Member_ID INTEGER NOT NULL,
                    Borrow_Date TEXT DEFAULT CURRENT_TIMESTAMP,
                    Due_Date TEXT,
                    Return_Date TEXT,
                    Status TEXT DEFAULT 'BORROWED',
                    FOREIGN KEY (Book_ID) REFERENCES Books(Book_ID) ON DELETE CASCADE,
                    FOREIGN KEY (Member_ID) REFERENCES Members(Member_ID) ON DELETE CASCADE
                )
                """;
            stmt.execute(createBorrowing);

            String createActivityLog = """
                CREATE TABLE IF NOT EXISTS ActivityLog (
                    Log_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    User_ID INTEGER NOT NULL,
                    Action_Type TEXT NOT NULL,
                    Details TEXT,
                    Log_Date TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (User_ID) REFERENCES Users(User_ID) ON DELETE CASCADE
                )
                """;
            stmt.execute(createActivityLog);

            String createCategories = """
                CREATE TABLE IF NOT EXISTS Categories (
                    Category_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Category_Name TEXT NOT NULL UNIQUE
                )
                """;
            stmt.execute(createCategories);

            String createReports = """
                CREATE TABLE IF NOT EXISTS Reports (
                    Report_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Report_Name TEXT NOT NULL,
                    Generated_Date TEXT DEFAULT CURRENT_TIMESTAMP,
                    Details TEXT
                )
                """;
            stmt.execute(createReports);

            UserDao userDao = new UserDao();
            if (userDao.getUserByUsername("admin") == null) {
                User defaultAdmin = new User(1, "admin", "admin123", "ADMIN", "APPROVED");
                userDao.addUser(defaultAdmin);
                System.out.println("[✓] Default Admin created — Username: admin | Password: admin123");
            }

            System.out.println("[✓] Database initialized — library.db");
        } catch (Exception e) {
            System.out.println("[✗] Init Error: " + e.getMessage());
        }
    }
}