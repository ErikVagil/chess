package server;

import spark.*;

import java.util.Map;

import com.google.gson.Gson;

import dataAccess.DataAccessException;
import service.ClearService;

public class ClearHandler {

    public static Object clear(Request req, Response res) {
        String body = "{}";
        try {
            ClearService.clear();
            res.status(200);
        } catch (DataAccessException e) {
            body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            res.type("application/json");
            res.status(500);
            res.body(body);
        }
        return body;
    }
}
