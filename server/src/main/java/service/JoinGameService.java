package service;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    
    public static void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("AuthToken not found");
        }
        GameData game = dao.getGame(gameID);
        if (game == null) {
            throw new RuntimeException("Bad request");
        }

        String joinedPlayerUsername = auth.username;
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername != null) throw new RuntimeException("Color already taken");
            GameData updatedGame = new GameData(gameID, 
                                                joinedPlayerUsername, 
                                                game.blackUsername, 
                                                game.gameName, 
                                                game.game);
            dao.updateGame(updatedGame);
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername != null) throw new RuntimeException("Color already taken");
            GameData updatedGame = new GameData(gameID, 
                                                game.whiteUsername, 
                                                joinedPlayerUsername, 
                                                game.gameName, 
                                                game.game);
            dao.updateGame(updatedGame);
        }
    }
}
