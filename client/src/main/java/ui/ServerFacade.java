package ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import chess.*;
import chess.ChessGame.TeamColor;
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
                        postLoginLoop(scanner);
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
                        postLoginLoop(scanner);
                    } catch (Exception e) {
                        System.out.println("Could not register. Please try again.");
                    }
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
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
                    break;
                case "logout":
                    try {
                        clientLogout(sessionToken);
                        System.out.println("Successfully logged out!");
                        running = false;
                        sessionToken = null;
                    } catch (Exception e) {
                        System.out.println("Could not log out. Please try again.");
                    }
                    break;
                case "create":
                    // Check command args
                    if (inputTokens.size() != 2) {
                        System.out.println("Proper usage of this command is:" +
                                           EscapeSequences.SET_TEXT_COLOR_BLUE +
                                           " create" +
                                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                           " <NAME>");
                        System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                                           "Type \"help\" for more information.");
                        break;
                    }

                    // Create game
                    String gameName = inputTokens.get(1);
                    try {
                        clientCreate(sessionToken, gameName);
                        System.out.println("Successfully created game!");
                    } catch (Exception e) {
                        System.out.println("Could not create game. Please try again.");
                    }
                    break;
                case "list":
                    try {
                        Collection<GameData> games = clientList(sessionToken);
                        for (GameData game : games) {
                            System.out.println("    NAME: " + game.gameName); 
                            System.out.println("      ID: " + game.gameID); 
                            System.out.println("   WHITE: " + game.whiteUsername); 
                            System.out.println("   BLACK: " + game.blackUsername + "\n");
                        }
                    } catch (Exception e) {
                        System.out.println("Could not get games list. Please try again.");
                    }
                    break;
                case "join":
                    // Check command args
                    if (inputTokens.size() < 2 || inputTokens.size() > 3) {
                        System.out.println("Proper usage of this command is:" +
                                           EscapeSequences.SET_TEXT_COLOR_BLUE +
                                           " join" +
                                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                                           " <ID> [WHITE|BLACK|<empty>]");
                        System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                                           "Type \"help\" for more information.");
                        break;
                    }

                    // Join a game
                    int gameID;
                    try {
                        gameID = Integer.parseInt(inputTokens.get(1));
                    } catch (ClassCastException e) {
                        System.out.println("Error: ID must be a number.");
                        break;
                    }

                    String color = null;
                    try {
                        color = inputTokens.get(2).toUpperCase();
                    } catch (IndexOutOfBoundsException e) {}

                    // Join game
                    try {
                        clientJoin(sessionToken, gameID, color);
                    } catch (Exception e) {
                        System.out.println("Could not join game. Please try again.");
                    }

                    // Draw boards -- change in phase 6
                    renderChessBoard(true);
                    renderChessBoard(false);
                    break;
                case "quit":
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                                       "Please log out before quitting." +
                                       EscapeSequences.RESET_TEXT_COLOR);
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }
    }

    public void renderChessBoard(boolean isFlipped) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        if (!isFlipped) {
            for (int row = 8; row >= 1; row--) {
                System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + row + "  ");
                for (int col = 1; col <= 8; col++) {
                    if ((row + col) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);

                    ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
                    String currentSymbol = getPieceSymbol(currentPiece);
                    System.out.print(currentSymbol);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
                System.out.println();
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            System.out.println(EscapeSequences.EMPTY + " a  b  c  d  e  f  g  h \n");
        } else {
            for (int row = 1; row <= 8; row++) {
                System.out.print(EscapeSequences.RESET_BG_COLOR + row + "  ");
                for (int col = 8; col >= 1; col--) {
                    if ((row + col) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                    

                    ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
                    String currentSymbol = getPieceSymbol(currentPiece);
                    System.out.print(currentSymbol);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
                System.out.println();
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            System.out.println(EscapeSequences.EMPTY + " h  g  f  e  d  c  b  a \n");
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;

        if (piece.getTeamColor() == TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KING;
                case QUEEN:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_QUEEN;
                case BISHOP:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_BISHOP;
                case KNIGHT:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KNIGHT;
                case ROOK:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_ROOK;
                case PAWN:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_PAWN;
                default:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.EMPTY;
            }
        } else {
            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KING;
                case QUEEN:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_QUEEN;
                case BISHOP:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_BISHOP;
                case KNIGHT:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KNIGHT;
                case ROOK:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_ROOK;
                case PAWN:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_PAWN;
                default:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.EMPTY;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public AuthData clientLogin(String username, String password) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/session");
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

    public AuthData clientRegister(String username, String password, String email) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/user");
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

    public void clientLogout(String authToken) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        http.connect();

        http.getResponseCode();
    }

    @SuppressWarnings("unchecked")
    public int clientCreate(String authToken, String gameName) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        Map<String, String> reqBody = Map.of("gameName", gameName);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Map<String, Double> resBody = new Gson().fromJson(inputStreamReader, Map.class);
            int gameID = (int) Math.round(resBody.get("gameID"));
            return gameID;
        }
    }

    public Collection<GameData> clientList(String authToken) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Type collectionType = new TypeToken<Map<String, Collection<GameData>>>(){}.getType();
            Map<String, Collection<GameData>> resBody = new Gson().fromJson(inputStreamReader, collectionType);
            return resBody.get("games");
        }
    }

    public void clientJoin(String authToken, int gameID, String playerColor) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("PUT");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        if (playerColor == null) playerColor = "null";
        Map<String, Object> reqBody = Map.of("playerColor", playerColor, "gameID", gameID);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        http.getResponseCode();
    }
}
