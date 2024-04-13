package webSocketMessages.userCommands;

import chess.ChessGame.TeamColor;

public class JoinPlayerCommand extends UserGameCommand {
    private final int gameID;
    private final TeamColor playerColor;

    public JoinPlayerCommand(String authToken, int gameID, TeamColor playerColor) {
        super(authToken);
        super.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public TeamColor getPlayerColor() {
        return playerColor;
    }
}
