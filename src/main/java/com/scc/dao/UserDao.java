package com.scc.dao;

import com.scc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String[] VALID_ROLES = {"ADMIN", "LIBRARIAN", "STUDENT"};
    private static final String[] VALID_STATUSES = {"PENDING", "APPROVED", "REJECTED", "SUSPENDED"};

    private boolean isValidRole(String role) {
        for (String r : VALID_ROLES) if (r.equals(role)) return true;
        return false;
    }

    private boolean isValidStatus(String status) {
        for (String s : VALID_STATUSES) if (s.equals(status)) return true;
        return false;
    }

    // ✅ REQUIRED: Update by User ID with uppercase conversion
    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String roleUpper = newRole.toUpperCase();
        String statusUpper = newStatus.toUpperCase();

        if (!isValidRole(roleUpper)) {
            System.out.println("[!] Invalid role: " + newRole);
            return false;
        }
        if (!isValidStatus(statusUpper)) {
            System.out.println("[!] Invalid status: " + newStatus);
            return false;
        }

        String sql = "UPDATE Users SET Role = ?, AccountStatus = ? WHERE User_ID = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roleUpper);
            pstmt.setString(2, statusUpper);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Update Error: " + e.getMessage());
            return false;
        }
    }

    public boolean addUser(User user) {
        if (!isValidRole(user.role())) {
            System.out.println("[!] Invalid role! Choose: ADMIN | LIBRARIAN | STUDENT");
            return false;
        }
        String status = user.accountStatus() != null ? user.accountStatus() : "PENDING";
        String sql = "INSERT INTO Users (Username, Password, Role, AccountStatus) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.username());
            pstmt.setString(2, user.password());
            pstmt.setString(3, user.role());
            pstmt.setString(4, status);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Add Error: " + e.getMessage());
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("AccountStatus")
                );
            }
        } catch (Exception e) {
            System.out.println("[✗] Fetch Error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY User_ID";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("AccountStatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("[✗] Get All Error: " + e.getMessage());
        }
        return list;
    }

    public List<User> getPendingUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE AccountStatus = 'PENDING' ORDER BY User_ID";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("AccountStatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("[✗] Pending Error: " + e.getMessage());
        }
        return list;
    }

    public boolean approveUser(String username) {
        String sql = "UPDATE Users SET AccountStatus = 'APPROVED' WHERE Username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Approve Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(String username, String newStatus) {
        String statusUpper = newStatus.toUpperCase();
        if (!isValidStatus(statusUpper)) return false;
        String sql = "UPDATE Users SET AccountStatus = ? WHERE Username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, statusUpper);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Status Update Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRole(String username, String newRole) {
        String roleUpper = newRole.toUpperCase();
        if (!isValidRole(roleUpper)) return false;
        String sql = "UPDATE Users SET Role = ? WHERE Username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roleUpper);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Role Update Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[✗] Delete Error: " + e.getMessage());
            return false;
        }
    }
}