package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

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

        Integer whiteUserID = getUserID(whiteUsername);
        Integer blackUserID = getUserID(blackUsername);

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
        GameData queriedGame = null;
        String statement = String.format("SELECT * FROM games WHERE gameID='%s'", gameID);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();
                
                while (rs.next()) {
                    int whiteUserID = rs.getInt("whiteUserID");
                    String whiteUsername = null;
                    String whiteStatement = String.format("SELECT username FROM users WHERE ID='%s'", whiteUserID);
                    try (PreparedStatement innerPreparedStatement = conn.prepareStatement(whiteStatement)) {
                        ResultSet innerRS = innerPreparedStatement.executeQuery();
                        while (innerRS.next()) {
                            whiteUsername = innerRS.getString("username");
                        }
                    }
                    int blackUserID = rs.getInt("blackUserID");
                    String blackUsername = null;
                    String blackStatement = String.format("SELECT username FROM users WHERE ID='%s'", blackUserID);
                    try (PreparedStatement innerPreparedStatement = conn.prepareStatement(blackStatement)) {
                        ResultSet innerRS = innerPreparedStatement.executeQuery();
                        while (innerRS.next()) {
                            blackUsername = innerRS.getString("username");
                        }
                    }
                    String gameName = rs.getString("gameName");
                    String gameJson = rs.getString("game");
                    ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);
                    queriedGame = new GameData(gameID,
                                               whiteUsername,
                                               blackUsername,
                                               gameName,
                                               chessGame);
                }
                
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
        return queriedGame;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String statement = "SELECT gameID FROM games";
        ArrayList<Integer> allGameIDs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    allGameIDs.add(rs.getInt("gameID"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }

        Collection<GameData> gamesList = new ArrayList<>();
        for (int gameID : allGameIDs) {
            gamesList.add(getGame(gameID));
        }

        return gamesList;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        int gameID = updatedGame.gameID;
        GameData existingGame = getGame(gameID);
        if (existingGame == null) return;
        String statement = String.format("DELETE FROM games WHERE gameID='%s'", gameID);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
        createGame(updatedGame);
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        Integer userID = getUserID(username);
        if (userID == null) throw new DataAccessException("User does not exist");
        String authToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO auths (authToken, userID) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setInt(2, userID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }
        return authToken;
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

    private Integer getUserID(String username) throws DataAccessException {
        String statement = String.format("SELECT ID FROM users WHERE username='%s'", username);
        ArrayList<Integer> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    result.add(null);
                } else {
                    result.add(rs.getInt("ID"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access database: %s", e.getMessage()));
        }

        return result.get(0);
    }
}
