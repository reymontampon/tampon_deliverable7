package com.scc.ui;

import java.util.Scanner;

public class StudentMenu {
    private final Scanner sc = new Scanner(System.in);

    public void show() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("         STUDENT DASHBOARD            ");
            System.out.println("=====================================");
            System.out.println("1. Search / Browse Books");
            System.out.println("2. My Borrowed Books");
            System.out.println("3. Request Book Borrowing");
            System.out.println("4. View Account Status");
            System.out.println("5. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> System.out.println("[✓] Searching books...");
                case "2" -> System.out.println("[✓] Showing your borrowed books...");
                case "3" -> System.out.println("[✓] Borrow request submitted...");
                case "4" -> System.out.println("[✓] Account Status: APPROVED");
                case "5" -> {
                    System.out.println("[✓] Logged out.");
                    return;
                }
                default -> System.out.println("[!] Invalid choice. Select 1–5.");
            }
        }
    }
}