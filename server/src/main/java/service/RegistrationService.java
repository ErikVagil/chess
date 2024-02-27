package service;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.UserData;

public class RegistrationService {
    
    public static String register(UserData user) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        UserData queriedUser = dao.getUser(user.getUsername());
        if (queriedUser != null) {
            throw new RuntimeException("User already exists in database");
        }
        dao.createUser(user);
        String authToken = dao.createAuth(user.getUsername());
        return authToken;
    }
}
