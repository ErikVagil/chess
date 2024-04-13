package webSocketMessages.serverMessages;

public class LoadGameMessage<T> extends ServerMessage {
    private final T game;

    public LoadGameMessage(T game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public T getGame() {
        return game;
    }
}
