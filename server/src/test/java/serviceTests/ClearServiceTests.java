package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import dataAccess.MemoryDatabase;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class ClearServiceTests {
    @Test
    public void clearTest() {
        DAO dao = new MemoryDAO();

        // Add dummy data to database
        try {
            dao.createUser(new UserData("finn", "pbgum", "finn@treehouse.com"));
            dao.createUser(new UserData("jake", "kimkilwon", "jake@treehouse.com"));
            dao.createUser(new UserData("marceline", "vampirebasslvr", "marcy@nightosphere.org"));
            dao.createGame(new GameData(10385, "finn", "jake", "atthetreehouse", null));
            dao.createGame(new GameData(69785, "marceline", "finn", "nooneleaves", null));
            dao.createAuth("finn");
            dao.createAuth("jake");
            dao.createAuth("finn");
            dao.createAuth("marceline");
        } catch (DataAccessException e) {}

        // Clear the database
        assertDoesNotThrow(ClearService::clear);

        // Check the db was cleared
        assertEquals(MemoryDatabase.userDB, new ArrayList<UserData>());
        assertEquals(MemoryDatabase.gameDB, new ArrayList<GameData>());
        assertEquals(MemoryDatabase.authDB, new ArrayList<AuthData>());
    }
}
