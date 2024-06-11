package com.mathedwin.main;

import com.mathedwin.main.model.Message;
import com.mathedwin.main.model.User;
import com.mathedwin.main.server.ChatServer;
import com.mathedwin.main.service.MessageService;
import com.mathedwin.main.service.UserService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        UserService userService = new UserService();
////        List<User> all = userService.getAllUsers();
////        all.forEach(System.out::println);
//
//        User user9 = new User();
//        user9.setUsername("user9");
//        user9.setPassword("password9");
//        user9.setEmail("user9@example.com");
////
//        userService.saveUser(user9);
//        MessageService messageService = new MessageService();
//
//        List<Message> allMessages = messageService.getLastMessages();
//        allMessages.forEach(System.out::println);
        new ChatServer().startServer();
    }
}