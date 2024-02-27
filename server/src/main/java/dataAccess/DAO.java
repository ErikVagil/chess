package dataAccess;

import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DAO {
    public void clear() throws DataAccessException;

    public void createUser(UserData user) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;

    public void createGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public void updateGame(int gameID) throws DataAccessException;

    public void createAuth(AuthData auth) throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
}