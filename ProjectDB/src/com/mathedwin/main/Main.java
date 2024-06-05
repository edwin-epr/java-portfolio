package com.mathedwin.main;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
//        userService.getAllUser();
//        userService.getUserById(5);
//        userService.getUserByUsername("\"user3\"");
//        userService.getUserByEmail("\"user4@example.com\"");
        userService.createUser("user6", "password6", "user6@example.com");
        // ALTER TABLE users AUTO_INCREMENT = 1;
    }

}