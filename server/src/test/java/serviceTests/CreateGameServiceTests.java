package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import service.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {
    
    @Test
    public void createGameTest() {
        // Add dummy data to database
        DAO dao = new MemoryDAO();
        String testAuth = "";
        try {
            testAuth = dao.createAuth("chowder");
        } catch (DataAccessException e) {}

        // Test createGame
        assertThrows(RuntimeException.class, () -> {
            CreateGameService.createGame("bad token", "gazpacho's stall");
        });
        final String testAuthFinal = testAuth;
        assertDoesNotThrow(() -> {
            CreateGameService.createGame(testAuthFinal, "mung's kitchen");
        });
    }
}
