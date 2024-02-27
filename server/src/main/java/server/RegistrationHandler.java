package server;

import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import model.UserData;
import service.RegistrationService;
import spark.*;

public class RegistrationHandler {
    
    public static Object register(Request req, Response res) {
        UserData user;
        String body;
        res.type("application/json");

        // Parse user data from HTTP request body
        try {
            user = Server.getBody(req, UserData.class);
        } catch (RuntimeException e) {
            // 400 bad request
            body = new Gson().toJson(Map.of("message", "Error: bad request"));
            res.status(400);
            res.body(body);
            return body;
        }

        // Register the user and get their authToken
        try {
            String authToken = RegistrationService.register(user);
            // 200 success
            body = new Gson().toJson(Map.of("username", user.getUsername(), "authToken", authToken));
            res.status(200);
            res.body(body);
            return body;
        } catch (RuntimeException e) {
            // 403 already taken
            body = new Gson().toJson(Map.of("message", "Error: already taken"));
            res.status(403);
            res.body(body);
            return body;
        } catch (DataAccessException e) {
            // 500 server error
            body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            res.status(500);
            res.body(body);
            return body;
        }
    }
}
