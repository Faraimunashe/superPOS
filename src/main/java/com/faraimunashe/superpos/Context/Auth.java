package com.faraimunashe.superpos.Context;

public class Auth {
    private static String token;
    private static User user;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Auth.token = token;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Auth.user = user;
    }

    public static class User {
        private int id;
        private String name;
        private String email;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
