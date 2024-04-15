package ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import model.*;
public class ServerFacade {
    private int port;
    private String sessionToken;
    private String displayName;
    
    public ServerFacade() {
        this.port = 8080;
        sessionToken = null;
        displayName = null;
    }

    public ServerFacade(int port) {
        this.port = port;
        sessionToken = null;
        displayName = null;
    }

    public void run() {
        System.out.println("♕  Welcome to CS 240 Chess. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        preLoginLoop(scanner);
        scanner.close();
    }

    private void preLoginLoop(Scanner scanner) {
        boolean running = true;
        
        while (running) {
            System.out.print("[" + EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED OUT" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ");

            String input = scanner.nextLine();

            // Parse input into tokens to get params
            List<String> inputTokens;
            inputTokens = Arrays.asList(input.split(" "));

            switch (inputTokens.get(0).toLowerCase()) {
                case "help":
                    printHelpLoggedOut();
                    break;
                case "quit":
                    running = printQuitLoggedOut();
                    break;
                case "login":
                    printLoginLoggedOut(inputTokens, scanner);
                    break;
                case "register":
                    printRegisterLoggedOut(inputTokens, scanner);
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }
    }

    private void printHelpLoggedOut() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    register" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <USERNAME> <PASSWORD> <EMAIL>" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Create a new account.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    login" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <USERNAME> <PASSWORD>" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Log in to an existing account.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    quit" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Exit the program.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    help" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Display information about commands.");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private boolean printQuitLoggedOut() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "Exiting program...");
        return false;
    }

    private void printLoginLoggedOut(List<String> inputTokens, Scanner scanner) {
        // Check command args
        if (inputTokens.size() != 3) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " login" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <USERNAME> <PASSWORD>");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }
        
        // Call login on server
        String username = inputTokens.get(1);
        String password = inputTokens.get(2);
        try {
            AuthData auth = FacadeFactory.clientLogin(username, password, port);
            sessionToken = auth.authToken;
            displayName = auth.username;
            System.out.println("Successfully logged in!");
            postLoginLoop(scanner);
        } catch (Exception e) {
            System.out.println("Could not log in. Please try again.");
        }
    }

    private void printRegisterLoggedOut(List<String> inputTokens, Scanner scanner) {
        // Check command args
        if (inputTokens.size() != 4) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " register" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <USERNAME> <PASSWORD> <EMAIL>");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }
        
        // Call register on server
        String username = inputTokens.get(1);
        String password = inputTokens.get(2);
        String email = inputTokens.get(3);
        try {
            AuthData auth = FacadeFactory.clientRegister(username, password, email, port);
            sessionToken = auth.authToken;
            displayName = auth.username;
            System.out.println("Successfully registered!");
            postLoginLoop(scanner);
        } catch (Exception e) {
            System.out.println("Could not register. Please try again.");
        }
    }

    private void postLoginLoop(Scanner scanner) {
        boolean running = true;
        
        while (running) {
            System.out.print("[" + EscapeSequences.SET_TEXT_COLOR_GREEN + displayName + EscapeSequences.RESET_TEXT_COLOR + "] >>> ");
            
            String input = scanner.nextLine();

            // Parse input into tokens to get params
            List<String> inputTokens;
            inputTokens = Arrays.asList(input.split(" "));

            switch (inputTokens.get(0).toLowerCase()) {
                case "help":
                    printHelpLoggedIn();
                    break;
                case "logout":
                    running = printLogoutLoggedIn();
                    break;
                case "create":
                    printCreateLoggedIn(inputTokens);
                    break;
                case "list":
                    printListLoggedIn();
                    break;
                case "join":
                    printJoinLoggedIn(inputTokens);
                    break;
                case "quit":
                    printQuitLoggedIn();
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }
    }

    private void printHelpLoggedIn() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    create" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <NAME>" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Create a new game.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    list" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - See a list of existing games.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    join" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <ID> [WHITE|BLACK|<empty>]" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Join an existing game. Leave color blank to join as an observer.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    logout" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Log out of the current session.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    help" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Display information about commands.");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private boolean printLogoutLoggedIn() {
        try {
            FacadeFactory.clientLogout(sessionToken, port);
            System.out.println("Successfully logged out!");
            sessionToken = null;
            return false;
        } catch (Exception e) {
            System.out.println("Could not log out. Please try again.");
        }
        return true;
    }

    private void printCreateLoggedIn(List<String> inputTokens) {
        // Check command args
        if (inputTokens.size() != 2) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " create" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <NAME>");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }

        // Create game
        String gameName = inputTokens.get(1);
        try {
            FacadeFactory.clientCreate(sessionToken, gameName, port);
            System.out.println("Successfully created game!");
        } catch (Exception e) {
            System.out.println("Could not create game. Please try again.");
        }
    }

    private void printListLoggedIn() {
        try {
            Collection<GameData> games = FacadeFactory.clientList(sessionToken, port);
            for (GameData game : games) {
                System.out.println("    NAME: " + game.gameName); 
                System.out.println("      ID: " + game.gameID); 
                System.out.println("   WHITE: " + game.whiteUsername); 
                System.out.println("   BLACK: " + game.blackUsername + "\n");
            }
        } catch (Exception e) {
            System.out.println("Could not get games list. Please try again.");
        }
    }

    private void printJoinLoggedIn(List<String> inputTokens) {
        // Check command args
        if (inputTokens.size() < 2 || inputTokens.size() > 3) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " join" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <ID> [WHITE|BLACK|<empty>]");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }

        // Join a game
        int gameID;
        try {
            gameID = Integer.parseInt(inputTokens.get(1));
        } catch (ClassCastException e) {
            System.out.println("Error: ID must be a number.");
            return;
        }

        String color = null;
        try {
            color = inputTokens.get(2).toUpperCase();
        } catch (IndexOutOfBoundsException e) {}

        // Join game
        try {
            FacadeFactory.clientJoin(sessionToken, gameID, color, port);
        } catch (Exception e) {
            System.out.println("Could not join game. Please try again.");
        }

        // Draw boards -- change in phase 6
        FacadeFactory.renderChessBoard(true);
        FacadeFactory.renderChessBoard(false);
    }

    private void printQuitLoggedIn() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                           "Please log out before quitting." +
                           EscapeSequences.RESET_TEXT_COLOR);
    }

    
}
