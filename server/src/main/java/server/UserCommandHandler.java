package server;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jetty.websocket.api.*;

import com.google.gson.Gson;

import chess.*;
import chess.ChessGame.TeamColor;
import server.PlayerConnection.PlayerType;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;
import dataAccess.*;
import model.*;

public class UserCommandHandler {
    private static DAO dao = new QueryDAO();
    private static HashMap<Integer, ArrayList<PlayerConnection>> playerConnections = new HashMap<>();

    public static void handleJoinPlayer(Session session, JoinPlayerCommand command) throws Exception {
        String authToken = command.getAuthString();
        AuthData authData = dao.getAuth(authToken);
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        try {
            checkCanJoin(session, authToken, gameID);
        } catch (Exception e) {
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
                sendErrorMessage(session, "Color already in use", true);
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
        } else if (findPlayerByAuth(gameID, authToken) != null) {
            removePlayerConnection(gameID, authToken);
        }
        playerConnections.get(gameID).add(connectionToSave);

        // Send load game response
        sendLoadGameMessage(session, game);

        // Send notification to other players
        sendNotifcationOtherPlayers(session, gameID, "Player " + username + " has joined the game.");
    }

    public static void handleJoinObserver(Session session, JoinObserverCommand command) throws Exception {
        String authToken = command.getAuthString();
        AuthData authData = dao.getAuth(authToken);
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        try {
            checkCanJoin(session, authToken, gameID);
        } catch (Exception e) {
            return;
        }
        
        // Store PlayerConnection for later lookup
        ChessGame game = gameData.game;
        String username = authData.username;

        PlayerConnection connectionToSave;
        connectionToSave = new PlayerConnection(session, gameID, username, PlayerType.OBSERVER);
        
        if (!playerConnections.containsKey(gameID)) {
            playerConnections.put(gameID, new ArrayList<PlayerConnection>());
        } else if (findPlayerByAuth(gameID, authToken) != null) {
            removePlayerConnection(gameID, authToken);
        }
        playerConnections.get(gameID).add(connectionToSave);

        // Send load game response
        sendLoadGameMessage(session, game);

        // Send notification to other players
        sendNotifcationOtherPlayers(session, gameID, "Observer " + username + " has joined the game.");
    }

    public static void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {
        // Check if player is in game and not observer
        PlayerConnection player = findPlayerByAuth(command.getGameID(), command.getAuthString());
        if (player == null) {
            sendErrorMessage(session, "Player is not in game");
            return;
        } else if (player.getPlayerType() == PlayerType.OBSERVER) {
            sendErrorMessage(session, "Observers cannot make moves");
            return;
        }

        // Get game object
        int gameID = command.getGameID();
        GameData gameData = dao.getGame(gameID);
        ChessGame game = gameData.game;

        // Check if game is over
        if (game.getIsGameOver()) {
            sendErrorMessage(session, "Game is over");
            return;
        }

        // Check if player's turn
        TeamColor teamTurn = game.getTeamTurn();
        TeamColor playerColor = player.getPlayerType() == PlayerType.WHITE_PLAYER ? TeamColor.WHITE : TeamColor.BLACK;
        if (teamTurn != playerColor) {
            sendErrorMessage(session, "Cannot make moves out of turn");
            return;
        }

        // Make move and check validity
        ChessMove move = command.getMove();
        try {
            game.makeMove(move);
            GameData newGameData = new GameData(gameID, 
                                                gameData.whiteUsername, 
                                                gameData.blackUsername, 
                                                gameData.gameName, 
                                                game);
            dao.updateGame(newGameData);
            for (PlayerConnection playerInRoom : playerConnections.get(gameID)) {
                sendLoadGameMessage(playerInRoom.getSession(), game);
            }
            if (teamTurn == TeamColor.WHITE) {
                sendNotifcationOtherPlayers(session, gameID, "White move " + move.toString());
            } else {
                sendNotifcationOtherPlayers(session, gameID, "Black move " + move.toString());
            }
        } catch (InvalidMoveException e) {
            sendErrorMessage(session, "Invalid move");
            return;
        }

        // Check if someone is in check, checkmate, or stalemate after move
        if (game.isInCheckmate(TeamColor.WHITE)) {
            sendNotifcationAllPlayers(gameID, "White is in checkmate");
        } else if (game.isInCheckmate(TeamColor.BLACK)) {
            sendNotifcationAllPlayers(gameID, "Black is in checkmate");
        } else if ((game.isInCheck(TeamColor.WHITE))) {
            sendNotifcationAllPlayers(gameID, "White is in check");
        } else if (game.isInCheck(TeamColor.BLACK)) {
            sendNotifcationAllPlayers(gameID, "Black is in check");
        } else if (game.isInStalemate(TeamColor.WHITE)) {
            sendNotifcationAllPlayers(gameID, "White is in stalemate");
        } else if (game.isInStalemate(TeamColor.BLACK)) {
            sendNotifcationAllPlayers(gameID, "Black is in stalemate");
        }

        // Check if game is over after move
        if (game.getIsGameOver()) {
            sendNotifcationAllPlayers(gameID, "Game over");
        }
    }

    public synchronized static void handleLeave(Session session, LeaveCommand command) throws Exception {
        // Check if player is in game
        PlayerConnection player = findPlayerByAuth(command.getGameID(), command.getAuthString());
        if (player == null) {
            sendErrorMessage(session, "Player is not in game");
            return;
        }

        // Get game object
        int gameID = command.getGameID();
        DAO dao = new QueryDAO();
        GameData gameData = dao.getGame(gameID);
        ChessGame game = gameData.game;

        // Remove player from the game and close the connection
        if (player.getPlayerType() == PlayerType.WHITE_PLAYER) {
            GameData newGameData = new GameData(gameID, 
                                        null, 
                                        gameData.blackUsername, 
                                        gameData.gameName, 
                                        game);
            dao.updateGame(newGameData);
        } else if (player.getPlayerType() == PlayerType.BLACK_PLAYER) {
            GameData newGameData = new GameData(gameID, 
                                        gameData.whiteUsername, 
                                        null, 
                                        gameData.gameName, 
                                        game);
            dao.updateGame(newGameData);
        }
        String username = player.getUsername();
        sendNotifcationOtherPlayers(session, gameID, username + " has left.");
        removePlayerConnection(gameID, command.getAuthString());
    }

    public static void handleResign(Session session, ResignCommand command) throws Exception {
        // Check if player is in game and not observer
        PlayerConnection player = findPlayerByAuth(command.getGameID(), command.getAuthString());
        if (player == null) {
            sendErrorMessage(session, "Player is not in game");
            return;
        } else if (player.getPlayerType() == PlayerType.OBSERVER) {
            sendErrorMessage(session, "Observers cannot resign");
            return;
        }

        // Get game object
        int gameID = command.getGameID();
        DAO dao = new QueryDAO();
        GameData gameData = dao.getGame(gameID);
        ChessGame game = gameData.game;

        // Check if game is over
        if (game.getIsGameOver()) {
            sendErrorMessage(session, "Game is already over");
            return;
        }

        // Resign
        game.setIsGameOver(true);
        GameData newGameData = new GameData(gameID, 
                                            gameData.whiteUsername, 
                                            gameData.blackUsername, 
                                            gameData.gameName, 
                                            game);
        dao.updateGame(newGameData);
        String username = player.getUsername();
        sendNotifcationAllPlayers(gameID, username + " has resigned the game.");
    }

    private static void checkCanJoin(Session session, String authToken, int gameID) throws Exception {
        // Check auth
        AuthData authData = dao.getAuth(authToken);
        if (authData == null) {
            sendErrorMessage(session, "User not authenticated");
            return;
        }
        
        // Check gameID
        GameData gameData = dao.getGame(gameID);
        if (gameData == null) {
            sendErrorMessage(session, "Invalid gameID");
            return;
        }
    }

    private static void sendLoadGameMessage(Session session, ChessGame game) throws Exception {
        LoadGameMessage loadResponse = new LoadGameMessage(game);
        String responseJson = new Gson().toJson(loadResponse);
        session.getRemote().sendString(responseJson);
    }

    private static void sendErrorMessage(Session session, String message) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(message);
        String errorJson = new Gson().toJson(errorMessage);
        session.getRemote().sendString(errorJson);
    }

    private static void sendErrorMessage(Session session, String message, boolean isCriticalError) throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(message, isCriticalError);
        String errorJson = new Gson().toJson(errorMessage);
        session.getRemote().sendString(errorJson);
    }

    private static void sendNotifcationOtherPlayers(Session session, int gameID, String message) throws Exception {
        NotificationMessage joinNotification = new NotificationMessage(message);
        String notificationJson = new Gson().toJson(joinNotification);

        for (PlayerConnection player : playerConnections.get(gameID)) {
            if (player.getSession().equals(session)) {
                continue;
            }
            
            player.getSession().getRemote().sendString(notificationJson);
        }
    }

    private static void sendNotifcationAllPlayers(int gameID, String message) throws Exception {
        NotificationMessage joinNotification = new NotificationMessage(message);
        String notificationJson = new Gson().toJson(joinNotification);

        for (PlayerConnection player : playerConnections.get(gameID)) {
            player.getSession().getRemote().sendString(notificationJson);
        }
    }

    private static PlayerConnection findPlayerByAuth(int gameID, String authToken) throws Exception {
        AuthData authData = dao.getAuth(authToken);
        String username = authData.username;
        ArrayList<PlayerConnection> gameConnections = playerConnections.get(gameID);
        for (PlayerConnection player : gameConnections) {
            if (username.equals(player.getUsername())) {
                return player;
            }
        }
        return null;
    }

    private static void removePlayerConnection(int gameID, String authToken) throws Exception {
        AuthData authData = dao.getAuth(authToken);
        String username = authData.username;
        ArrayList<PlayerConnection> gameConnections = playerConnections.get(gameID);
        ArrayList<PlayerConnection> newConnections = new ArrayList<>();
        for (PlayerConnection player : gameConnections) {
            if (!username.equals(player.getUsername())) {
                newConnections.add(player);
            } else {
                player.getSession().close();
            }
        }
        playerConnections.put(gameID, newConnections);
    }
}
