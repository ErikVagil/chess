package dataAccess;

import java.sql.SQLException;
import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public class QueryDAO implements DAO {
    public QueryDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {}
    }

    @Override
    public void clear() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
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
    public void updateGame(GameData updatedGame) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
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

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS users (
          ID int NOT NULL AUTO_INCREMENT,
          username varchar(256) NOT NULL,
          password varchar(256) NOT NULL,
          email varchar(256) NOT NULL,
          PRIMARY KEY (ID)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS games (
          gameID int NOT NULL,
          whiteUserID int,
          blackUserID int,
          gameName varchar(256) NOT NULL,
          game text,
          PRIMARY KEY (gameID),
          FOREIGN KEY (whiteUserID) REFERENCES users(ID),
          FOREIGN KEY (blackUserID) REFERENCES users(ID)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS auths (
          ID int NOT NULL AUTO_INCREMENT,
          authToken varchar(256) NOT NULL,
          userID int NOT NULL,
          PRIMARY KEY (ID),
          FOREIGN KEY (userID) REFERENCES users(ID)
        )
        """
    };
    
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
