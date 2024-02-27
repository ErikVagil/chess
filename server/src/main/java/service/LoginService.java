package service;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.UserData;

public class LoginService {
    
    public static String login(String username, String password) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        UserData user = dao.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Username or password is incorrect");
        }
        String authToken = dao.createAuth(username);
        return authToken;
    }
}
