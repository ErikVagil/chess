package ui;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import dataAccess.DAO;
import dataAccess.QueryDAO;
import model.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage.ServerMessageType;
import webSocketMessages.userCommands.*;

public class WebSocketFacade extends Endpoint {
    private Session session;
    private ChessGame game;
    private TeamColor color = null;
    private String authToken;
    private int gameID;
    private boolean running = true;
    private String prompt;

    public WebSocketFacade(int port) throws Exception {
        URI uri = new URI("ws://localhost:" + port + "/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public synchronized void onMessage(String message) {
                handleMessage(message);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    public synchronized void run(Scanner scanner, String authToken, int gameID, String colorString, String displayName) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        
        // Generate personalized input prompt
        prompt = generatePrompt(color, gameID);
        
        // Send join message
        try {
            color = TeamColor.valueOf(colorString);
            JoinPlayerCommand joinCommand = new JoinPlayerCommand(authToken, gameID, color);
            String commandString = new Gson().toJson(joinCommand);
            session.getBasicRemote().sendText(commandString);
        } catch (NullPointerException e) {
            JoinObserverCommand joinCommand = new JoinObserverCommand(authToken, gameID);
            String commandString = new Gson().toJson(joinCommand);
            session.getBasicRemote().sendText(commandString);
        }

        // Run IO loop
        while (running) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            // Parse input into tokens to get params
            List<String> inputTokens;
            inputTokens = Arrays.asList(input.split(" "));

            switch (inputTokens.get(0).toLowerCase()) {
                case "help":
                    printHelp();
                    break;
                case "redraw":
                    drawBoard();
                    break;
                case "move":
                    printMove(inputTokens);
                    break;
                case "resign":
                    printResign();
                    break;
                case "legal":
                    printLegal(inputTokens);
                    break;
                case "leave":
                    running = printLeave();
                    break;
                default:
                    System.out.println("Please enter a valid command. Type \"help\" for more information.");
                    break;
            }
        }
    }

    private void handleMessage(String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        ServerMessageType messageType = ServerMessageType.valueOf(json.get("serverMessageType").getAsString());
        switch (messageType) {
            case LOAD_GAME:
                LoadGameMessage loadMessage = new Gson().fromJson(message, LoadGameMessage.class);
                game = loadMessage.getGame();
                drawBoard();
                System.out.print(prompt);
                break;
            case ERROR:
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + errorMessage.getErrorMessage() + EscapeSequences.RESET_TEXT_COLOR);
                if (errorMessage.isCriticalError()) {
                    running = false;
                } else {
                    System.out.print(prompt);
                }
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println("\nNotification: " + notificationMessage.getMessage());
                System.out.print(prompt);
                break;
            default:
                break;
        }
    }

    private String generatePrompt(TeamColor color, int gameID) throws Exception {
        DAO dao = new QueryDAO();
        GameData game = dao.getGame(gameID);
        String gameName = game.gameName;
        String prompt;
        if (color == null) {
            prompt = "[" + EscapeSequences.SET_TEXT_COLOR_BLUE + gameName + ": Observer" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ";
        } else if (color == TeamColor.WHITE) {
            prompt = "[" + EscapeSequences.SET_TEXT_COLOR_BLUE + gameName + ": White" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ";
        } else {
            prompt = "[" + EscapeSequences.SET_TEXT_COLOR_BLUE + gameName + ": Black" + EscapeSequences.RESET_TEXT_COLOR + "] >>> ";
        }
        return prompt;
    }

    private void printHelp() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    move" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <MOVE>" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Make a chess move. Moves must be in algebraic format, e.g. \"e2e4\"");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    legal" +
                           EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                           " <POSITION>" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Show the legal moves for a piece at a position.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    resign" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Resign the game. The game will end but you will not be kicked out.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    redraw" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Draw the board again.");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + 
                           "    leave" +
                           EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +
                           " - Leave the game.");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void drawBoard() {
        if (color == null || color == TeamColor.WHITE) {
            FacadeFactory.renderChessBoardWhite(game, null);
        } else {
            FacadeFactory.renderChessBoardBlack(game, null);
        }
    }

    private boolean printLeave() throws Exception {
        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameID);
        String commandString = new Gson().toJson(leaveCommand);
        session.getBasicRemote().sendText(commandString);
        return false;
    }

    private void printMove(List<String> inputTokens) throws Exception {
        // Check command args
        if (inputTokens.size() != 2) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " move" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <MOVE>");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }

        ChessPosition startPos, endPos;
        try {
            startPos = algebraicToPosition(inputTokens.get(1).substring(0, 2));
            endPos = algebraicToPosition(inputTokens.get(1).substring(2, 4));
        } catch (IllegalArgumentException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + 
                               "Error: Positions must be in proper algebraic notation." + 
                               EscapeSequences.RESET_TEXT_COLOR);
            return;
        } catch (IndexOutOfBoundsException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + 
                               "Error: Positions must be in proper algebraic notation." + 
                               EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, new ChessMove(startPos, endPos));
        String commandString = new Gson().toJson(moveCommand);
        session.getBasicRemote().sendText(commandString);
    }

    private void printResign() throws Exception {
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        String commandString = new Gson().toJson(resignCommand);
        session.getBasicRemote().sendText(commandString);
    }

    private void printLegal(List<String> inputTokens) {
        // Check command args
        if (inputTokens.size() != 2) {
            System.out.println("Proper usage of this command is:" +
                               EscapeSequences.SET_TEXT_COLOR_BLUE +
                               " legal" +
                               EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                               " <POSITION>");
            System.out.println(EscapeSequences.RESET_TEXT_COLOR +
                               "Type \"help\" for more information.");
            return;
        }

        ChessPosition piecePos;
        try {
            piecePos = algebraicToPosition(inputTokens.get(1));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Positions must be in proper algebraic notation.");
            return;
        }

        if (color == null || color == TeamColor.WHITE) {
            FacadeFactory.renderChessBoardWhite(game, piecePos);
        } else {
            FacadeFactory.renderChessBoardBlack(game, piecePos);
        }
    }

    private ChessPosition algebraicToPosition(String algebraicPos) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (!algebraicPos.matches("[a-hA-H]\\d")) {
            throw new IllegalArgumentException("Improper format");
        }

        int col = algebraicPos.charAt(0) - 96;
        int row = Integer.parseInt(algebraicPos.substring(1));

        return new ChessPosition(row, col);
    }
}
