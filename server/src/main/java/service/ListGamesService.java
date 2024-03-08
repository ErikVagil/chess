package service;

import java.util.Collection;

import dataAccess.*;
import model.AuthData;
import model.GameData;

public class ListGamesService {
    
    public static Collection<GameData> listGames(String authToken) throws DataAccessException, RuntimeException {
        DAO dao = new QueryDAO();
        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("AuthToken not found");
        }
        Collection<GameData> games = dao.listGames();
        return games;
    }
}
