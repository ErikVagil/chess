package dataAccessTests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import dataAccess.*;
import model.*;

public class QueryDAOTests {
    
    @Test
    public void createUserTestPos() {
        DAO dao = new QueryDAO();
        UserData testUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                         "testPassword" + (int)(Math.random() * 100000000), 
                                         "testEmail" + (int)(Math.random() * 100000000));
        assertDoesNotThrow(() -> dao.createUser(testUser));

        String statement = String.format("SELECT username, password, email FROM users WHERE username='%s'", testUser.username);
        Map<String, Object> result = new HashMap<String, Object>();
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        result.put("username", rs.getString("username"));
                        result.put("password", rs.getString("password"));
                        result.put("email", rs.getString("email"));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
            }
        });
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertEquals(testUser.username, result.get("username"));
        assertTrue(encoder.matches(testUser.password, result.get("password").toString()));
        assertEquals(testUser.email, result.get("email"));
    }

    @Test
    public void createUserTestNeg() {
        DAO dao = new QueryDAO();
        UserData testUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                         null, 
                                         "testEmail" + (int)(Math.random() * 100000000));
        assertThrows(IllegalArgumentException.class, () -> dao.createUser(testUser));
    }

    @Test
    public void getUserTestPos() {
        DAO dao = new QueryDAO();
        UserData testUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                         "testPassword" + (int)(Math.random() * 100000000), 
                                         "testEmail" + (int)(Math.random() * 100000000));
        assertDoesNotThrow(() -> dao.createUser(testUser));
        Map<String, UserData> result = new HashMap<>();
        assertDoesNotThrow(() -> result.put("got", dao.getUser(testUser.username)));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertNotNull(result.get("got"));
        assertEquals(testUser.username, result.get("got").username);
        assertTrue(encoder.matches(testUser.password, result.get("got").password));
        assertEquals(testUser.email, result.get("got").email);
    }

    @Test
    public void getUserTestNeg() {
        DAO dao = new QueryDAO();
        Map<String, UserData> results = new HashMap<>();
        assertDoesNotThrow(() -> results.put("got", dao.getUser("definitely not a username")));
        assertNull(results.get("got"));
    }

    @Test
    public void createGameTestPos() {
        DAO dao = new QueryDAO();
        GameData testGame = new GameData((int)(Math.random() * 100000000), 
                                         null, 
                                         null, 
                                         "testGame" + (int)(Math.random() * 100000000), 
                                         null);
        assertDoesNotThrow(() -> dao.createGame(testGame));

        String statement = String.format("SELECT * FROM games WHERE gameID='%s'", testGame.gameID);
        Map<String, Object> result = new HashMap<String, Object>();
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        result.put("gameID", rs.getString("gameID"));
                        result.put("gameName", rs.getString("gameName"));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
            }
        });
        
        assertEquals(testGame.gameID, Integer.parseInt(result.get("gameID").toString()));
        assertEquals(testGame.gameName, result.get("gameName"));
    }

    @Test
    public void createGameTestNeg() {
        DAO dao = new QueryDAO();
        GameData testGame = new GameData(0, null, null, null, null);
        assertThrows(DataAccessException.class, () -> dao.createGame(testGame));
    }

    @Test
    public void getGameTestPos() {
        DAO dao = new QueryDAO();
        UserData testWhiteUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                              "testPassword" + (int)(Math.random() * 100000000), 
                                              "testEmail" + (int)(Math.random() * 100000000));
        assertDoesNotThrow(() -> dao.createUser(testWhiteUser));

        UserData testBlackUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                              "testPassword" + (int)(Math.random() * 100000000), 
                                              "testEmail" + (int)(Math.random() * 100000000));
        assertDoesNotThrow(() -> dao.createUser(testBlackUser));

        GameData testGame = new GameData((int)(Math.random() * 100000000), 
                                         testWhiteUser.username, 
                                         testBlackUser.username,
                                         "testGameName" + (int)(Math.random() * 100000000),
                                         null);
        assertDoesNotThrow(() -> dao.createGame(testGame));
        Map<String, GameData> result = new HashMap<>();
        assertDoesNotThrow(() -> result.put("got", dao.getGame(testGame.gameID)));

        assertNotNull(result.get("got"));
        assertEquals(testGame.gameID, result.get("got").gameID);
        assertEquals(testGame.whiteUsername, result.get("got").whiteUsername);
        assertEquals(testGame.blackUsername, result.get("got").blackUsername);
        assertEquals(testGame.gameName, result.get("got").gameName);
        assertEquals(testGame.game, result.get("got").game);
    }

    @Test
    public void getGameTestNeg() {
        DAO dao = new QueryDAO();
        Map<String, GameData> results = new HashMap<>();
        assertDoesNotThrow(() -> results.put("got", dao.getGame(-1)));
        assertNull(results.get("got"));
    }

    @Test
    public void listGamesTest() {
        DAO dao = new QueryDAO();

        // Clear database first
        assertDoesNotThrow(() -> {
            String[] statements = {"DROP TABLE games",
                                   "DROP TABLE auths",
                                   "DROP TABLE users"};
                                   try (Connection conn = DatabaseManager.getConnection()) {
                                       for (String statement : statements) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
            }
        });

        new QueryDAO();

        GameData testGame1 = new GameData(101, null, null, "test1", null);
        GameData testGame2 = new GameData(102, null, null, "test2", null);
        assertDoesNotThrow(() -> dao.createGame(testGame1));
        assertDoesNotThrow(() -> dao.createGame(testGame2));

        ArrayList<Collection<GameData>> gotListList = new ArrayList<>();
        assertDoesNotThrow(() -> gotListList.add(dao.listGames()));
        Collection<GameData> gotList = gotListList.get(0);
        assertEquals(2, gotList.size());
    }

    @Test
    public void updateGameTest() {
        DAO dao = new QueryDAO();

        // Clear database first
        assertDoesNotThrow(() -> {
            String[] statements = {"DROP TABLE games",
                                "DROP TABLE auths",
                                "DROP TABLE users"};
                                try (Connection conn = DatabaseManager.getConnection()) {
                                    for (String statement : statements) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
            }
        });

        new QueryDAO();

        GameData testGame = new GameData(201, null, null, "test1", null);
        assertDoesNotThrow(() -> dao.createGame(testGame));

        UserData testWhiteUser = new UserData("testUser" + (int)(Math.random() * 100000000), 
                                              "testPassword" + (int)(Math.random() * 100000000), 
                                              "testEmail" + (int)(Math.random() * 100000000));
        assertDoesNotThrow(() -> dao.createUser(testWhiteUser));

        assertDoesNotThrow(() -> dao.updateGame(new GameData(testGame.gameID, testWhiteUser.username, null, testGame.gameName, null)));
        ArrayList<String> result = new ArrayList<>();
        assertDoesNotThrow(() -> result.add(dao.getGame(testGame.gameID).whiteUsername));
        assertEquals(testWhiteUser.username, result.get(0));
    }
}
