package serviceTests;

import org.junit.jupiter.api.Test;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.UserData;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTests {
    
    @Test
    public void registerTest() {
        // Add dummy data to database
        DAO dao = new MemoryDAO();
        try {
            dao.createUser(new UserData("mordecai", "wontcleanthepark", "bluejay@park.org"));
        } catch (DataAccessException e) {}

        // Success test
        assertDoesNotThrow(() -> {
            RegistrationService.register(new UserData("benson", "getbacktowork", "gumballs@park.org"));
        });
        
        // 403 test
        assertThrows(RuntimeException.class, () -> {
            RegistrationService.register(new UserData("mordecai", "rigbysbetter", "raccoon@park.org"));
        });
    }
}
