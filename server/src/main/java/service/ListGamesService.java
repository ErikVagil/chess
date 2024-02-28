package service;

import java.util.Collection;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.AuthData;
import model.GameData;

public class ListGamesService {
    
    public static Collection<GameData> listGames(String authToken) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("AuthToken not found");
        }
        Collection<GameData> games = dao.listGames();
        return games;
    }
}
