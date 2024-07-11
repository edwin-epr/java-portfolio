package org.mathedwin.softdev.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class PromptServer {
    private static final String CLEAR_CHARACTER = "\033[H\033[2J";
    public static final String WELCOME_BANNER =
            """
                     /$$$$$$$$       /$$               /$$                     \s
                    | $$_____/      | $$              |__/                     \s
                    | $$        /$$$$$$$ /$$  /$$  /$$ /$$ /$$$$$$$            \s
                    | $$$$$    /$$__  $$| $$ | $$ | $$| $$| $$__  $$           \s
                    | $$__/   | $$  | $$| $$ | $$ | $$| $$| $$  \\ $$           \s
                    | $$      | $$  | $$| $$ | $$ | $$| $$| $$  | $$           \s
                    | $$$$$$$$|  $$$$$$$|  $$$$$/$$$$/| $$| $$  | $$           \s
                    |________/ \\_______/ \\_____\\___/ |__/|__/  |__/           \s
                                                                               \s
                                                                               \s
                                                                               \s
                      /$$$$$$                                                  \s
                     /$$__  $$                                                 \s
                    | $$  \\__/  /$$$$$$   /$$$$$$  /$$    /$$ /$$$$$$   /$$$$$$\s
                    |  $$$$$$  /$$__  $$ /$$__  $$|  $$  /$$//$$__  $$ /$$__  $$
                     \\____  $$| $$$$$$$$| $$  \\__/ \\  $$/$$/| $$$$$$$$| $$  \\__/
                     /$$  \\ $$| $$_____/| $$        \\  $$$/ | $$_____/| $$     \s
                    |  $$$$$$/|  $$$$$$$| $$         \\  $/  |  $$$$$$$| $$     \s
                     \\______/  \\_______/|__/          \\_/    \\_______/|__/     \s
                    """;

    public static void welcome(PrintWriter out, BufferedReader in) throws IOException {
        clearScreen(out);
        out.println(PromptServer.WELCOME_BANNER);
        out.print("Welcome! Remember to be friendly and respectful, press ENTER to continue...");
        out.flush();
        in.readLine();
        clearScreen(out);
    }

    public static void clearScreen(PrintWriter out) {
        out.print(CLEAR_CHARACTER);
        out.flush();
    }

    public static int startScreen (PrintWriter out, BufferedReader in) throws IOException {
        int option;
        while (true) {
            clearScreen(out);
            out.println("1) Log in");
            out.println("2) Create account");
            out.println("3) Exit");
            out.print("Please select an option: ");
            out.flush();
            try {
                option = Integer.parseInt(in.readLine());
            } catch (RuntimeException e) {
                option = 0;
            }
            clearScreen(out);
            if (option == 1 || option == 2 || option == 3) {
                return option;
            } else {
                out.println("Invalid option, try again.");
                out.print("Press ENTER to continue...");
                out.flush();
                in.readLine();
                clearScreen(out);
            }
        }
    }
}
