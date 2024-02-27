package dataAccess;

import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DAO {
    public void clear() throws DataAccessException;

    public void createUser() throws DataAccessException;
    public UserData getUser() throws DataAccessException;

    public void createGame() throws DataAccessException;
    public GameData getGame() throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public void updateGame() throws DataAccessException;

    public void createAuth() throws DataAccessException;
    public AuthData getAuth() throws DataAccessException;
    public void deleteAuth() throws DataAccessException;
}