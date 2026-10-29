package com.scc.model;

public record User(
        int userId,          // NEW
        String username,
        String password,
        String role,
        String accountStatus
) {}