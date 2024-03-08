package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.gson.Gson;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

public class QueryDAO implements DAO {
    /*
        String statement = "";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
    */
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
        String username = user.username;
        String password = user.password;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        String email = user.email;
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData queriedUser = null;
        String statement = String.format("SELECT username, password, email FROM users WHERE username='%s'", username);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();
                
                while (rs.next()) {
                    String gotUsername = rs.getString("username");
                    String gotPassword = rs.getString("password");
                    String gotEmail = rs.getString("email");
                    queriedUser = new UserData(gotUsername, gotPassword, gotEmail);
                }
                
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
        return queriedUser;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        int gameID = game.gameID;
        String whiteUsername = game.whiteUsername;
        String blackUsername = game.blackUsername;
        String gameName = game.gameName;
        ChessGame chessGame = game.game;
        String gameJson = new Gson().toJson(chessGame);

        // Get user IDs
        String whiteStatement = String.format("SELECT ID FROM users WHERE username='%s'", whiteUsername);
        String blackStatement = String.format("SELECT ID FROM users WHERE username='%s'", blackUsername);
        Map<String, Integer> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(whiteStatement)) {
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    result.put("whiteID", rs.getInt("ID"));
                }
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(blackStatement)) {
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    result.put("blackID", rs.getInt("ID"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
        Integer whiteUserID = result.get("whiteID");
        Integer blackUserID = result.get("blackID");

        // Create game
        String statement = "INSERT INTO games (gameID, whiteUserID, blackUserID, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                if (whiteUserID == null) {
                    preparedStatement.setString(2, null);
                } else {
                    preparedStatement.setInt(2, whiteUserID);
                }
                if (blackUserID == null) {
                    preparedStatement.setString(3, null);
                } else {
                    preparedStatement.setInt(3, blackUserID);
                }
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, gameJson);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
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
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }
}
