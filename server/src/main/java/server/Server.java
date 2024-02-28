package server;

import com.google.gson.Gson;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", ClearHandler::clear);
        Spark.post("/user", RegistrationHandler::register);
        Spark.post("/session", LoginHandler::login);
        Spark.delete("/session", LogoutHandler::logout);
        Spark.get("/game", ListGamesHandler::listGames);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static <T> T getBody(Request req, Class<T> cl) throws RuntimeException {
        var body = new Gson().fromJson(req.body(), cl);
        if (body == null) {
            throw new RuntimeException("Request missing body");
        }
        return body;
    }
}