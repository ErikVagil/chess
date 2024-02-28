package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.GameData;
import service.JoinGameService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTests {
    
    @Test
    public void joinGameTest() {
        DAO dao = new MemoryDAO();
        String testToken = "";
        try {
            testToken = dao.createAuth("pops");
            dao.createGame(new GameData(101, null, null, "test", null));
        } catch (DataAccessException e) {}

        final String testTokenFinal = testToken;
        assertDoesNotThrow(() -> JoinGameService.joinGame(testTokenFinal, "WHITE", 101));
        assertThrows(RuntimeException.class, () -> JoinGameService.joinGame("bad token", "WHITE", 101));
        assertThrows(RuntimeException.class, () -> JoinGameService.joinGame(testTokenFinal, "WHITE", 0));
    }
}
