package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {
    private final Scanner sc = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void start() {
        System.out.println("\n===== USER LOGIN =====");
        System.out.print("Username: ");
        String uname = sc.nextLine().trim();
        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();

        User user = userDao.getUserByUsername(uname);
        if (user == null) {
            System.out.println("[✗] Username not found.");
            return;
        }
        if (!user.password().equals(pwd)) {
            System.out.println("[✗] Incorrect password.");
            return;
        }
        if (!"APPROVED".equals(user.accountStatus())) {
            System.out.println("[!] Account not approved. Wait for Admin.");
            return;
        }

        System.out.println("[✓] Login SUCCESSFUL! Welcome, " + uname + " [" + user.role() + "]");

        switch (user.role()) {
            case "ADMIN" -> new AdminMenu(user).show();
            case "LIBRARIAN" -> new LibrarianMenu().show();
            case "STUDENT" -> new StudentMenu().show();
            default -> System.out.println("[✗] Unknown role.");
        }
    }
}