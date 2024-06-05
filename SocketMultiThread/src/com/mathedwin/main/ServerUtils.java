package com.mathedwin.main;

import java.io.PrintWriter;

public class ServerUtils {

    private static final String CLEAR_CHARACTER = "\033[H\033[2J";

    private static final int DEFAULT_WAITING_TIME = 1000;

    public static void clearConsole(PrintWriter output) {
        output.print(CLEAR_CHARACTER);
    }

    public static void countDown(PrintWriter output) throws InterruptedException {
        for (int seconds = 10; seconds > 0; seconds--) {
            clearConsole(output);
            output.printf("Cuenta regresiva: %d segundos restantes...%n", seconds);
            Thread.sleep(DEFAULT_WAITING_TIME);
        }
    }
}
