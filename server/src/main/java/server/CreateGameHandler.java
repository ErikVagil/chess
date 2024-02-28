package server;

import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import service.CreateGameService;
import spark.*;

public class CreateGameHandler {
    
    @SuppressWarnings("unchecked")
    public static Object createGame(Request req, Response res) {
        String body = "{}";
        res.type("application/json");
        res.body(body);

        // Get header and body data
        String gameName = null;
        String authToken = req.headers("authorization");
        try {
            Map<String, String> reqBody = Server.getBody(req, Map.class);
            gameName = reqBody.get("gameName");
            if (authToken == null ||
                gameName == null) {
                throw new RuntimeException("Missing info");
            }
        } catch (RuntimeException e) {
            // 400 bad request
            res.status(400);
            body = new Gson().toJson(Map.of("message", "Error: bad request"));
            return body;
        }

        // Create a new game
        try {
            int gameID = CreateGameService.createGame(authToken, gameName);
            body = new Gson().toJson(Map.of("gameID", gameID));
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
