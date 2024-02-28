package service;

import java.util.Random;

import chess.ChessGame;
import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    
    public static int createGame(String authToken, String gameName) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("AuthToken not found");
        }
        int gameID = 0;
        while (true) {
            gameID = new Random().nextInt(1_000_000_000) + 100_000_000;
            if (dao.getGame(gameID) == null) break;
        }
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        dao.createGame(newGame);
        return gameID;
    }
}
