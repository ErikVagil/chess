package ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import model.*;
public class ServerFacade {
    private boolean loggedIn;
    private String sessionToken;
    private String displayName;
    
    public ServerFacade() {
        loggedIn = false;
        sessionToken = null;
        displayName = null;
    }

    public void run() {
        boolean running = true;

        System.out.println("♕  Welcome to CS 240 Chess. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        
        while (!loggedIn && running) {
            System.out.print("[" + EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED OUT" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ");
            String input = scanner.nextLine();

            // Parse input into tokens to get params
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
                    // Check command args
                    if (inputTokens.size() != 3) {
                        System.out.println("Proper usage of this command is:" +
                                           EscapeSequences.SET_TEXT_COLOR_BLUE +
                                           " login" +
                                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                           " <USERNAME> <PASSWORD>");
                        System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                                           "Type \"help\" for more information.");
                        break;
                    }
                    
                    // Call login on server
                    String username = inputTokens.get(1);
                    String password = inputTokens.get(2);
                    try {
                        AuthData auth = clientLogin(username, password);
                        sessionToken = auth.authToken;
                        displayName = auth.username;
                        System.out.println("Successfully logged in!");
                    } catch (Exception e) {
                        System.out.println("Could not log in. Please try again.");
                    }
                    break;
                case "register":
                    // Check command args
                    if (inputTokens.size() != 4) {
                        System.out.println("Proper usage of this command is:" +
                                           EscapeSequences.SET_TEXT_COLOR_BLUE +
                                           " register" +
                                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                           " <USERNAME> <PASSWORD> <EMAIL>");
                        System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                                           "Type \"help\" for more information.");
                        break;
                    }
                    
                    // Call register on server
                    username = inputTokens.get(1);
                    password = inputTokens.get(2);
                    String email = inputTokens.get(3);
                    try {
                        AuthData auth = clientRegister(username, password, email);
                        sessionToken = auth.authToken;
                        displayName = auth.username;
                        System.out.println("Successfully registered!");
                    } catch (Exception e) {
                        System.out.println("Could not register. Please try again.");
                    }
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }

        scanner.close();
    }

    @SuppressWarnings("unchecked")
    private AuthData clientLogin(String username, String password) throws Exception {
        URI uri = new URI("http://localhost:8080/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        Map<String, String> reqBody = Map.of("username", username, "password", password);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Map<String, String> res = new Gson().fromJson(inputStreamReader, Map.class);
            return new AuthData(res.get("authToken"), res.get("username"));
        }
    }

    private AuthData clientRegister(String username, String password, String email) throws Exception {
        URI uri = new URI("http://localhost:8080/user");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        UserData reqBody = new UserData(username, password, email);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody, UserData.class);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new Gson().fromJson(inputStreamReader, model.AuthData.class);
        }
    }
}
