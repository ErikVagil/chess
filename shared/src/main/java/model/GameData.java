package model;

import chess.ChessGame;

public class GameData {
    private int gameID;
    private String whiteUsername, blackUsername, gameName;
    private ChessGame game;

    public GameData(int gameID, 
                    String whiteUsername, 
                    String blackUsername, 
                    String gameName, 
                    ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {
        return this.gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return this.whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return this.blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return this.gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
