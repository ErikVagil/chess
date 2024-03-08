package service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dataAccess.*;
import model.UserData;

public class LoginService {
    
    public static String login(String username, String password) throws DataAccessException, RuntimeException {
        DAO dao = new QueryDAO();
        UserData user = dao.getUser(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user == null || !encoder.matches(password, user.password)) {
            throw new RuntimeException("Username or password is incorrect");
        }
        String authToken = dao.createAuth(username);
        return authToken;
    }
}
