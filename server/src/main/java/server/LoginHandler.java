package server;

import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import service.LoginService;
import spark.*;

public class LoginHandler {
    
    @SuppressWarnings("unchecked")
    public static Object login(Request req, Response res) {
        Map<String, String> credentials;
        String body = null;
        res.type("application/json");
        res.body(body);

        // Get HTTP header body
        try {
            credentials = Server.getBody(req, Map.class);
        } catch (RuntimeException e) {
            // 400 bad request
            body = new Gson().toJson(Map.of("message", "Error: bad request"));
            res.status(400);
            return body;
        }

        // Login to the database
        String username = credentials.get("username");
        String password = credentials.get("password");
        try {
            String authToken = LoginService.login(username, password);
            body = new Gson().toJson(Map.of("username", username, "authToken", authToken));
        } catch (RuntimeException e) {
            // 401 unauthorized
            body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
        } catch (DataAccessException e) {
            // 500 server error
            body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            res.status(500);
        }

        return body;
    }
}
