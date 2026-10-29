package com.scc.ui;

import java.util.Scanner;

public class LibrarianMenu {
    private final Scanner sc = new Scanner(System.in);

    public void show() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("       LIBRARIAN DASHBOARD            ");
            System.out.println("=====================================");
            System.out.println("1. Manage Books (Add/Edit/Remove)");
            System.out.println("2. View All Members");
            System.out.println("3. Process Borrowing Request");
            System.out.println("4. Process Book Return");
            System.out.println("5. View Overdue Books");
            System.out.println("6. Generate Reports");
            System.out.println("7. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> System.out.println("[✓] Book Management opened...");
                case "2" -> System.out.println("[✓] Showing all members...");
                case "3" -> System.out.println("[✓] Processing borrowing...");
                case "4" -> System.out.println("[✓] Processing return...");
                case "5" -> System.out.println("[✓] Showing overdue list...");
                case "6" -> System.out.println("[✓] Generating reports...");
                case "7" -> {
                    System.out.println("[✓] Logged out.");
                    return;
                }
                default -> System.out.println("[!] Invalid choice. Select 1–7.");
            }
        }
    }
}