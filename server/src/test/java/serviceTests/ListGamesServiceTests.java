package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.GameData;
import service.ListGamesService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesServiceTests {
    
    @Test
    public void listGamesTest() {
        // Add dummy data to database
        String auth = "";
        Collection<GameData> testGames = new ArrayList<>();
        DAO dao = new MemoryDAO();
        try {
            auth = dao.createAuth("kai");

            GameData testData0 = new GameData(1234, "kai", "jay", "best element battle", null);
            dao.createGame(testData0);
            testGames.add(testData0);

            GameData testData1 = new GameData(7777, "lloyd", "nya", "bffs", null);
            dao.createGame(testData1);
            testGames.add(testData1);
        } catch (DataAccessException e) {}

        // Test listGames
        Collection<GameData> testList = null;
        try {
            testList = ListGamesService.listGames(auth);
        } catch (DataAccessException e) {} catch (RuntimeException e) {}
        assertEquals(testList, testGames);

        assertThrows(RuntimeException.class, () -> {
            ListGamesService.listGames("bad token");
        });
    }
}
