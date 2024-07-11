package org.mathedwin.softdev;

import org.mathedwin.softdev.server.ChatServer;
public class Main {
    public static void main(String[] args) {
        new ChatServer().startServer();
    }
}