package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc = new Scanner(System.in);
    private final UserDao userDao = new UserDao();
    private final User currentAdmin;

    public AdminMenu(User currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.equals("0")) return 0;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number! Enter a number or 0 to cancel.");
            }
        }
    }

    private String readValidRole(String prompt) {
        List<String> allowedRoles = List.of("ADMIN", "LIBRARIAN", "STUDENT");
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("0")) return "0";
            if (allowedRoles.contains(input)) return input;
            System.out.println("[!] Invalid role! Allowed: ADMIN | LIBRARIAN | STUDENT");
        }
    }

    // ✅ FIXED: Only PENDING & APPROVED
    private String readValidStatus(String prompt) {
        List<String> allowedStatus = List.of("PENDING", "APPROVED");
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("0")) return "0";
            if (allowedStatus.contains(input)) return input;
            System.out.println("[!] Invalid status! Allowed: PENDING | APPROVED");
        }
    }

    private void displayUserTable(List<User> users) {
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-6s | %-15s | %-12s | %-12s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("----------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-6d | %-15s | %-12s | %-12s%n",
                    u.userId(), u.username(), u.role(), u.accountStatus());
        }
        System.out.println("----------------------------------------------------------------------");
    }

    public void show() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("          ADMIN DASHBOARD            ");
            System.out.println("=====================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Approvals");
            System.out.println("3. Approve User");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role & Status");
            System.out.println("6. Delete User");
            System.out.println("7. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingApprovals();
                case "3" -> approveUser();
                case "4" -> addNewUser();
                case "5" -> updateUserRoleAndStatusMenu();
                case "6" -> deleteUser();
                case "7" -> {
                    System.out.println("[✓] Logged out successfully.");
                    return;
                }
                default -> System.out.println("[!] Invalid choice! Select 1–7.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        displayUserTable(users);
    }

    private void viewPendingApprovals() {
        System.out.println("\n--- PENDING APPROVALS ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }
        displayUserTable(pending);
    }

    private void approveUser() {
        viewPendingApprovals();
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) return;

        System.out.print("\nEnter Username to approve (0 = Cancel): ");
        String input = sc.nextLine().trim();
        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }
        boolean exists = pending.stream().anyMatch(u -> u.username().equals(input));
        if (!exists) {
            System.out.println("[!] Username not in pending list.");
            return;
        }
        if (userDao.approveUser(input)) {
            System.out.println("[✓] User approved! Status → APPROVED");
        } else {
            System.out.println("[✗] Approval failed.");
        }
    }

    private void addNewUser() {
        System.out.println("\n--- ADD NEW USER ---");
        System.out.print("Username: ");
        String uname = sc.nextLine().trim();
        if (uname.isEmpty()) {
            System.out.println("[!] Username cannot be empty.");
            return;
        }

        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();
        if (pwd.isEmpty()) {
            System.out.println("[!] Password cannot be empty.");
            return;
        }

        String role = readValidRole("Role (ADMIN | LIBRARIAN | STUDENT): ");
        if (role.equals("0")) {
            System.out.println("Add user cancelled.");
            return;
        }

        String status = readValidStatus("Status (PENDING | APPROVED): ");
        if (status.equals("0")) {
            System.out.println("Add user cancelled.");
            return;
        }

        if (userDao.addUser(new User(0, uname, pwd, role, status))) {
            System.out.println("[✓] User added successfully!");
        } else {
            System.out.println("[✗] Failed! Username may already exist.");
        }
    }

    private void updateUserRoleAndStatusMenu() {
        viewAllUsers();

        int targetId = readIntInput("\nEnter User ID to update (0 = Cancel): ");
        if (targetId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (targetId == currentAdmin.userId()) {
            System.out.println("\n⚠️  WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole("New Role (ADMIN | LIBRARIAN | STUDENT): ");
        if (newRole.equals("0")) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus("New Status (PENDING | APPROVED): ");
        if (newStatus.equals("0")) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
            System.out.println("[✓] User updated successfully!");
        } else {
            System.out.println("[✗] Update failed! Check User ID.");
        }
    }

    private void deleteUser() {
        viewAllUsers();
        System.out.print("\nEnter Username to DELETE (0 = Cancel): ");
        String uname = sc.nextLine().trim();
        if (uname.equals("0")) {
            System.out.println("Delete cancelled.");
            return;
        }

        if (uname.equals(currentAdmin.username())) {
            System.out.println("[!] You cannot delete your own account.");
            return;
        }

        System.out.print("Confirm DELETE? YES/NO: ");
        if (sc.nextLine().trim().equalsIgnoreCase("YES")) {
            if (userDao.deleteUser(uname)) {
                System.out.println("[✓] User deleted successfully!");
            } else {
                System.out.println("[✗] Delete failed! Username not found.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }
}