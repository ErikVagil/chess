package service;

import dataAccess.DAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO;
import model.AuthData;

public class LogoutService {

    public static void logout(String authToken) throws DataAccessException, RuntimeException {
        DAO dao = new MemoryDAO();
        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("AuthToken not found");
        }
        dao.deleteAuth(authToken);
    }
}
