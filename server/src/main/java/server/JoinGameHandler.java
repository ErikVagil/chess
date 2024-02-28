package server;

import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import service.JoinGameService;
import spark.*;

public class JoinGameHandler {
    
    @SuppressWarnings("unchecked")
    public static Object joinGame(Request req, Response res) {
        String body = "{}";
        res.type("application/json");
        res.body(body);

        // Get header and body data
        String playerColor = null;
        int gameID = -1;
        String authToken = req.headers("authorization");
        Map<String, Object> reqBody;
        try {
            reqBody = Server.getBody(req, Map.class);
            playerColor = (String)reqBody.get("playerColor");
            gameID = (int)(((Double)reqBody.get("gameID")).doubleValue());
        } catch (RuntimeException e) {
            // 400 bad request
            res.status(400);
            body = new Gson().toJson(Map.of("message", "Error: bad request"));
            return body;
        }

        // Join a game
        try {
            if (authToken == null ||
                gameID == -1) {
                throw new RuntimeException("Bad request");
            }
            JoinGameService.joinGame(authToken, playerColor, gameID);
            res.status(200);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Bad request")) {
                // 400 bad request
                res.status(400);
                body = new Gson().toJson(Map.of("message", "Error: bad request"));
            } else if (e.getMessage().equals("AuthToken not found")) {
                // 401 unauthorized
                body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
                res.status(401);
            } else if (e.getMessage().equals("Color already taken")) {
                // 403 already taken
                body = new Gson().toJson(Map.of("message", "Error: already taken"));
                res.status(403);
            }
        } catch (DataAccessException e) {
            // 500 server error
            body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            res.status(500);
        }
        return body;
    }
}
