package dataAccess;

import java.util.ArrayList;
import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDAO implements DAO {

    @Override
    public void clear() throws DataAccessException {
        MemoryDatabase.userDB = new ArrayList<>();
        MemoryDatabase.gameDB = new ArrayList<>();
        MemoryDatabase.authDB = new ArrayList<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        MemoryDatabase.userDB.add(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        MemoryDatabase.gameDB.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void updateGame(int gameID) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        MemoryDatabase.authDB.add(auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }
}
