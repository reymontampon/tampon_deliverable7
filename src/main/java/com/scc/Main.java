package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize database & tables
        DatabaseInitializer.init();

        // Ensure default admin exists (ID=1)
        UserDao userDao = new UserDao();
        if (userDao.getUserByUsername("admin") == null) {
            User defaultAdmin = new User(1, "admin", "admin123", "ADMIN", "APPROVED");
            userDao.addUser(defaultAdmin);
            System.out.println("[✓] Default Admin created — Username: admin | Password: admin123");
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. LOGIN");
            System.out.println("2. REGISTER");
            System.out.println("3. EXIT");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> new Authentication().start();
                case "2" -> {
                    System.out.println("\n--- STUDENT REGISTRATION ---");
                    System.out.print("Username: ");
                    String u = sc.nextLine().trim();
                    System.out.print("Password: ");
                    String p = sc.nextLine().trim();

                    if (u.isEmpty() || p.isEmpty()) {
                        System.out.println("[!] Username and Password cannot be empty.");
                        break;
                    }

                    // Auto-assign role=STUDENT, status=PENDING
                    User newStudent = new User(0, u, p, "STUDENT", "PENDING");
                    if (userDao.addUser(newStudent)) {
                        System.out.println("[✓] Registered successfully! Wait for Admin approval.");
                    } else {
                        System.out.println("[✗] Registration failed! Username already exists.");
                    }
                }
                case "3" -> {
                    System.out.println("Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("[!] Invalid choice! Please select 1, 2, or 3.");
            }
        }
    }
}