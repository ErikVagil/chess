package server;

import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import model.GameData;
import service.ListGamesService;
import spark.*;

public class ListGamesHandler {
    
    public static Object listGames(Request req, Response res) {
        String body = "{}";
        res.type("application/json");
        res.body(body);

        String authToken = req.headers("authorization");
        if (authToken == null) {
            // 400 bad request
            res.status(400);
            body = new Gson().toJson(Map.of("message", "Error: bad request"));
            return body;
        }

        try {
            Collection<GameData> games = ListGamesService.listGames(authToken);
            // 200 success
            res.status(200);
            body = new Gson().toJson(games, Collection.class);
        } catch (RuntimeException e) {
            // 401 unauthorized
            res.status(401);
            body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            // 500 server error
            res.status(500);
            body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
        return body;
    }
}
