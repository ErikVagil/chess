package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class ServerFacade {
    private boolean loggedIn;
    
    public ServerFacade() {
        this.loggedIn = false;
    }

    public void run() {
        boolean running = true;

        System.out.println("♕  Welcome to CS 240 Chess. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        
        while (!loggedIn && running) {
            System.out.print("[" + EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED OUT" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ");
            String input = scanner.nextLine();
            List<String> inputTokens;
            inputTokens = Arrays.asList(input.split(" "));

            switch (inputTokens.get(0).toLowerCase()) {
                case "help":
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                                       "    register" +
                                       EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                       " <USERNAME> <PASSWORD> <EMAIL>" +
                                       EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                                       " - create a new account");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                                       "    login" +
                                       EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                       " <USERNAME> <PASSWORD>" +
                                       EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                                       " - log in to an existing account");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                                       "    quit" +
                                       EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                                       " - exit the program");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                                       "    help" +
                                       EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                                       " - display information about commands");
                    System.out.print(EscapeSequences.RESET_TEXT_COLOR);
                    break;
                case "quit":
                    running = false;
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + 
                                       "Exiting program...");
                    break;
                case "login":
                    System.out.println(inputTokens.toString());
                    break;
                case "register":
                    System.out.println(inputTokens.toString());
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }

        scanner.close();
    }
}
