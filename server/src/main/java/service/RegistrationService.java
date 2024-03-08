package service;

import dataAccess.*;
import model.UserData;

public class RegistrationService {
    
    public static String register(UserData user) throws DataAccessException, RuntimeException {
        DAO dao = new QueryDAO();
        UserData queriedUser = dao.getUser(user.username);
        if (queriedUser != null) {
            throw new RuntimeException("User already exists in database");
        }
        dao.createUser(user);
        String authToken = dao.createAuth(user.username);
        return authToken;
    }
}
