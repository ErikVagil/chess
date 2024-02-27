package dataAccess;

import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DAO {
    public void clear();

    public void createUser();
    public UserData getUser();

    public void createGame();
    public GameData getGame();
    public Collection<GameData> listGames();
    public void updateGame();

    public void createAuth();
    public AuthData getAuth();
    public void deleteAuth();
}