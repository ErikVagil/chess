package dataAccessTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.MemoryDAO;
import dataAccess.MemoryDatabase;
import model.UserData;
import model.AuthData;
import model.GameData;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryDAOTests {
    
    @Test
    public void clearTest() {
        MemoryDatabase.userDB = new ArrayList<>();
        MemoryDatabase.userDB.add(new UserData("a", "b", "c"));
        MemoryDatabase.userDB.add(new UserData("b", "c", "d"));
        MemoryDatabase.userDB.add(new UserData("c", "d", "e"));

        MemoryDatabase.gameDB = new ArrayList<>();
        MemoryDatabase.gameDB.add(new GameData(101, null, null, "test", null));

        MemoryDatabase.authDB = new ArrayList<>();
        MemoryDatabase.authDB.add(new AuthData("token0", "a"));
        MemoryDatabase.authDB.add(new AuthData("token1", "b"));

        DAO dao = new MemoryDAO();

        assertDoesNotThrow(() -> dao.clear());
        assertEquals(new ArrayList<UserData>(), MemoryDatabase.userDB);
        assertEquals(new ArrayList<GameData>(), MemoryDatabase.gameDB);
        assertEquals(new ArrayList<AuthData>(), MemoryDatabase.authDB);
    }

    @Test
    public void createUserTest() {
        MemoryDatabase.userDB = new ArrayList<>();
        
        DAO dao = new MemoryDAO();
        assertDoesNotThrow(() -> dao.createUser(new UserData("a", "b", "c")));
        assertNotEquals(new ArrayList<UserData>(), MemoryDatabase.userDB);
    }

    @Test
    public void getUserTest() {
        MemoryDatabase.userDB = new ArrayList<>();
        UserData testUser = new UserData("a", "b", "c");
        MemoryDatabase.userDB.add(testUser);
        
        DAO dao = new MemoryDAO();
        UserData gotUser = assertDoesNotThrow(() -> dao.getUser("a"));
        assertEquals(testUser, gotUser);
        gotUser = assertDoesNotThrow(() -> dao.getUser("not in database"));
        assertEquals(null, gotUser);
    }

    @Test
    public void createGameTest() {
        MemoryDatabase.gameDB = new ArrayList<>();
        
        DAO dao = new MemoryDAO();
        assertDoesNotThrow(() -> dao.createGame(new GameData(101, null, null, "test", null)));
        assertNotEquals(new ArrayList<GameData>(), MemoryDatabase.gameDB);
    }

    @Test
    public void getGameTest() {
        MemoryDatabase.gameDB = new ArrayList<>();
        GameData testGame = new GameData(101, null, null, "test", null);
        MemoryDatabase.gameDB.add(testGame);
        
        DAO dao = new MemoryDAO();
        GameData gotGame = assertDoesNotThrow(() -> dao.getGame(101));
        assertEquals(testGame, gotGame);
        gotGame = assertDoesNotThrow(() -> dao.getGame(666));
        assertEquals(null, gotGame);
    }

    @Test
    public void listGamesTest() {
        MemoryDatabase.gameDB = new ArrayList<>();
        MemoryDatabase.gameDB.add(new GameData(101, null, null, "test", null));
        MemoryDatabase.gameDB.add(new GameData(102, null, null, "test again", null));

        DAO dao = new MemoryDAO();
        Collection<GameData> gotGames = assertDoesNotThrow(() -> dao.listGames());
        assertEquals(gotGames, MemoryDatabase.gameDB);
    }

    @Test
    public void updateGameTest() {
        MemoryDatabase.gameDB = new ArrayList<>();
        GameData testGame = new GameData(101, null, null, "test", null);
        MemoryDatabase.gameDB.add(testGame);
        
        DAO dao = new MemoryDAO();
        GameData updatedGame = new GameData(101, "test", null, "test", null);
        assertDoesNotThrow(() -> dao.updateGame(updatedGame));
        assertEquals(1, MemoryDatabase.gameDB.size());
        assertEquals(updatedGame, MemoryDatabase.gameDB.get(0));
    }

    @Test
    public void createAuthTest() {
        MemoryDatabase.authDB = new ArrayList<>();
        
        DAO dao = new MemoryDAO();
        assertDoesNotThrow(() -> dao.createAuth("a"));
        assertNotEquals(new ArrayList<AuthData>(), MemoryDatabase.authDB);
    }

    @Test
    public void getAuthTest() {
        MemoryDatabase.authDB = new ArrayList<>();
        AuthData testAuth = new AuthData("token", "username");
        MemoryDatabase.authDB.add(testAuth);
        
        DAO dao = new MemoryDAO();
        AuthData gotAuth = assertDoesNotThrow(() -> dao.getAuth("token"));
        assertEquals(testAuth, gotAuth);
        gotAuth = assertDoesNotThrow(() -> dao.getAuth("not in database"));
        assertEquals(null, gotAuth);
    }

    @Test
    public void deleteAuthTest() {
        MemoryDatabase.authDB = new ArrayList<>();
        AuthData testAuth = new AuthData("token", "username");
        MemoryDatabase.authDB.add(testAuth);
        
        DAO dao = new MemoryDAO();
        assertDoesNotThrow(() -> dao.deleteAuth("token"));
        assertEquals(new ArrayList<AuthData>(), MemoryDatabase.authDB);
    }
}
