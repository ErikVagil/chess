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
import model.GameData;

public class UserCommandHandler {
    private static HashMap<Integer, ArrayList<PlayerConnection>> playerConnections = new HashMap<>();

    public static void handleJoinPlayer(Session session, JoinPlayerCommand command) throws Exception {
        // Check auth
        DAO dao = new QueryDAO();
        String authToken = command.getAuthString();
        if (dao.getAuth(authToken) == null) {
            throw new RuntimeException("authToken not found");
        }

        // Store PlayerConnection for later lookup
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        ChessGame game = gameData.game;
        TeamColor playerColor = command.getPlayerColor();

        PlayerConnection connectionToSave;
        String username;
        if (playerColor == TeamColor.WHITE) {
            username = gameData.whiteUsername;
            connectionToSave = new PlayerConnection(session, gameID, username, PlayerType.WHITE_PLAYER);
        } else {
            username = gameData.blackUsername;
            connectionToSave = new PlayerConnection(session, gameID, username, PlayerType.BLACK_PLAYER);
        }
        
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

    }

    public static void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {

    }

    public static void handleLeave(Session session, LeaveCommand command) throws Exception {

    }

    public static void handleResign(Session session, ResignCommand command) throws Exception {

    }
}
