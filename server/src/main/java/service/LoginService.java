package service;

import dataAccess.*;
import model.UserData;

public class LoginService {
    
    public static String login(String username, String password) throws DataAccessException, RuntimeException {
        DAO dao = new QueryDAO();
        UserData user = dao.getUser(username);
        if (user == null || !user.password.equals(password)) {
            throw new RuntimeException("Username or password is incorrect");
        }
        String authToken = dao.createAuth(username);
        return authToken;
    }
}
