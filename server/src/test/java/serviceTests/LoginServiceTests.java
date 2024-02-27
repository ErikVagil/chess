package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.UserData;
import service.LoginService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    
    @Test
    public void loginTest() {
        // Add dummy data to database
        DAO dao = new MemoryDAO();
        try {
            dao.createUser(new UserData("gumball", "whatthewatterson", "bluecat@elmore.net"));
            dao.createUser(new UserData("darwin", "noimnotfishpanic", "orangefish@elmore.net"));
        } catch (DataAccessException e) {}

        // 200 tests
        assertDoesNotThrow(() -> {
            LoginService.login("gumball", "whatthewatterson");
        });
        assertDoesNotThrow(() -> {
            LoginService.login("darwin", "noimnotfishpanic");
        });

        // 401 tests
        assertThrows(RuntimeException.class, () -> {
            LoginService.login("guball", "whatthewatterson");
        });
        assertThrows(RuntimeException.class, () -> {
            LoginService.login("darwin", "fishbowl");
        });
        assertThrows(RuntimeException.class, () -> {
            LoginService.login("gumball", "ilovepenny");
        });
    }
}
