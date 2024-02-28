package model;

import chess.ChessGame;

public class GameData {
    public int gameID;
    public String whiteUsername, blackUsername, gameName;
    public ChessGame game;

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
}
