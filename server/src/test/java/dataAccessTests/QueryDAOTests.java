package dataAccessTests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import dataAccess.*;
import model.UserData;

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
}
