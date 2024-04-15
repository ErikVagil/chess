package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;

import com.google.gson.*;

import spark.Request;
import spark.Spark;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.userCommands.*;
import webSocketMessages.userCommands.UserGameCommand.CommandType;

@WebSocket
public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        
        // WebSocket upgrade endpoint
        Spark.webSocket("/connect", Server.class);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", ClearHandler::clear);
        Spark.post("/user", RegistrationHandler::register);
        Spark.post("/session", LoginHandler::login);
        Spark.delete("/session", LogoutHandler::logout);
        Spark.get("/game", ListGamesHandler::listGames);
        Spark.post("/game", CreateGameHandler::createGame);
        Spark.put("/game", JoinGameHandler::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static <T> T getBody(Request req, Class<T> cl) throws RuntimeException {
        T body = new Gson().fromJson(req.body(), cl);
        if (body == null) {
            throw new RuntimeException("Request missing body");
        }
        return body;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        CommandType commandType = CommandType.valueOf(json.get("commandType").getAsString());
        switch (commandType) {
            case JOIN_PLAYER:
                JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                UserCommandHandler.handleJoinPlayer(session, joinPlayerCommand);
                break;
            case JOIN_OBSERVER:
                JoinObserverCommand joinObserverCommand = new Gson().fromJson(message, JoinObserverCommand.class);
                UserCommandHandler.handleJoinObserver(session, joinObserverCommand);
                break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                UserCommandHandler.handleMakeMove(session, makeMoveCommand);
                break;
            case LEAVE:
                LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                UserCommandHandler.handleLeave(session, leaveCommand);
                break;
            case RESIGN:
                ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                UserCommandHandler.handleResign(session, resignCommand);
                break;
            default:
                ErrorMessage errorMessage = new ErrorMessage("Invalid command");
                String errorString = new Gson().toJson(errorMessage);
                session.getRemote().sendString(errorString);
        }
    }
}