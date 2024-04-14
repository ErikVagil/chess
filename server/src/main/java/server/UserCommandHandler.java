package server;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jetty.websocket.api.*;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import server.PlayerConnection.PlayerType;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;
import dataAccess.*;
import model.*;

public class UserCommandHandler {
    private static HashMap<Integer, ArrayList<PlayerConnection>> playerConnections = new HashMap<>();

    public static void handleJoinPlayer(Session session, JoinPlayerCommand command) throws Exception {
        // Check auth
        DAO dao = new QueryDAO();
        String authToken = command.getAuthString();
        AuthData authData = dao.getAuth(authToken);
        if (authData == null) {
            sendErrorMessage(session, "User not authenticated");
            return;
        }

        // Check gameID
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        if (gameData == null) {
            sendErrorMessage(session, "Invalid gameID");
            return;
        }

        // Check correct color and player exists in database
        TeamColor playerColor = command.getPlayerColor();
        String username = authData.username;
        if (playerColor == TeamColor.WHITE) {
            if (!username.equals(gameData.whiteUsername)) {
                sendErrorMessage(session, "Color already in use");
                return;
            } else if (gameData.whiteUsername == null) {
                sendErrorMessage(session, "Invalid player");
                return;
            }
        } else {
            if (!username.equals(gameData.blackUsername)) {
                sendErrorMessage(session, "Color already in use");
                return;
            } else if (gameData.blackUsername == null) {
                sendErrorMessage(session, "Invalid player");
                return;
            }
        }
        
        // Store PlayerConnection for later lookup
        ChessGame game = gameData.game;

        PlayerConnection connectionToSave;
        PlayerType playerType = playerColor == TeamColor.WHITE ? PlayerType.WHITE_PLAYER : PlayerType.BLACK_PLAYER;
        connectionToSave = new PlayerConnection(session, gameID, username, playerType);
        
        if (!playerConnections.containsKey(gameID)) {
            playerConnections.put(gameID, new ArrayList<PlayerConnection>());
        }
        playerConnections.get(gameID).add(connectionToSave);

        // Send load game response
        LoadGameMessage<ChessGame> loadResponse = new LoadGameMessage<ChessGame>(game);
        String responseJson = new Gson().toJson(loadResponse);
        session.getRemote().sendString(responseJson);

        // Send notification to other players
        NotificationMessage joinNotification = new NotificationMessage("Player " + username + " has joined the game.");
        String notificationJson = new Gson().toJson(joinNotification);

        for (PlayerConnection player : playerConnections.get(gameID)) {
            if (player.getSession().equals(session)) {
                continue;
            }
            
            player.getSession().getRemote().sendString(notificationJson);
        }
    }

    public static void handleJoinObserver(Session session, JoinObserverCommand command) throws Exception {
        // Check auth
        DAO dao = new QueryDAO();
        String authToken = command.getAuthString();
        AuthData authData = dao.getAuth(authToken);
        if (authData == null) {
            sendErrorMessage(session, "User not authenticated");
            return;
        }

        // Check gameID
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        if (gameData == null) {
            sendErrorMessage(session, "Invalid gameID");
            return;
        }
        
        // Store PlayerConnection for later lookup
        ChessGame game = gameData.game;
        String username = authData.username;

        PlayerConnection connectionToSave;
        connectionToSave = new PlayerConnection(session, gameID, username, PlayerType.OBSERVER);
        
        if (!playerConnections.containsKey(gameID)) {
            playerConnections.put(gameID, new ArrayList<PlayerConnection>());
        }
        playerConnections.get(gameID).add(connectionToSave);

        // Send load game response
        LoadGameMessage<ChessGame> loadResponse = new LoadGameMessage<ChessGame>(game);
        String responseJson = new Gson().toJson(loadResponse);
        session.getRemote().sendString(responseJson);

        // Send notification to other players
        NotificationMessage joinNotification = new NotificationMessage("Observer " + username + " has joined the game.");
        String notificationJson = new Gson().toJson(joinNotification);

        for (PlayerConnection player : playerConnections.get(gameID)) {
            if (player.getSession().equals(session)) {
                continue;
            }
            
            player.getSession().getRemote().sendString(notificationJson);
        }
    }

    public static void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {

    }

    public static void handleLeave(Session session, LeaveCommand command) throws Exception {

    }

    public static void handleResign(Session session, ResignCommand command) throws Exception {

    }

    private static void sendErrorMessage(Session session, String message) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(message);
        String errorJson = new Gson().toJson(errorMessage);
        session.getRemote().sendString(errorJson);
        throw new RuntimeException(message);
    }
}
