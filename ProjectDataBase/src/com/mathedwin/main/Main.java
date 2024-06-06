package com.mathedwin.main;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
//        userService.getAllUser();
//        userService.getUserById(7);
        userService.getUserByUsername("user8");
//        userService.getUserByEmail("user9@example.com");
//        userService.createUser("user8", "password8", "user8@example.com");
//        userService.getUserById(8);
//        userService.deleteUserByUsername("user8");
//        userService.updateUserById(8, "user8", "password8", "user8@example.com");
    }
}