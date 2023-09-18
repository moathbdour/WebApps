package org.example;

public class User {
    private int userId;
    private String username;
    private String name;
    private String role;
    private String password;

    public User(int userId, String username, String name, String role, String password) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}

