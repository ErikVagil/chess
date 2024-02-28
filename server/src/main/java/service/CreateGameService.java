package service;

import java.util.UUID;

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
        int gameID = UUID.randomUUID().hashCode();
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        dao.createGame(newGame);
        return gameID;
    }
}
