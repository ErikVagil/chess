package service;

import dataAccess.*;

public class ClearService {
    
    public static void clear() throws DataAccessException {
        DAO dao = new QueryDAO();
        dao.clear();
    }
}
