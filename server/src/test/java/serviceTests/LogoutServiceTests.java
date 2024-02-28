package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.UserData;
import service.LogoutService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {

    @Test
    public void logoutTest() {
        // Add dummy data to database
        DAO dao = new MemoryDAO();
        String testAuthToken = "";
        try {
            dao.createUser(new UserData("stevenuniverse", "notmymom", "suniverse@beachcity.com"));
            testAuthToken = dao.createAuth("stevenuniverse");
        } catch (DataAccessException e) {}

        // Test logout
        final String testTokenFinal = testAuthToken;
        assertDoesNotThrow(() -> {
            LogoutService.logout(testTokenFinal);
        });
        assertThrows(RuntimeException.class, () -> {
            LogoutService.logout("definitely not a uuid");
        });
    }
}
