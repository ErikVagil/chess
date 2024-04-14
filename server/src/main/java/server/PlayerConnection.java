package server;

import org.eclipse.jetty.websocket.api.Session;

public class PlayerConnection {
    public enum PlayerType {
        WHITE_PLAYER,
        BLACK_PLAYER,
        OBSERVER
    }

    private Session session;
    private int gameID;
    private String username;
    private PlayerType playerType;

    public PlayerConnection(Session session, int gameID, String username, PlayerType playerType) {
        this.session = session;
        this.gameID = gameID;
        this.playerType = playerType;
    }

    public Session getSession() {
        return session;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
