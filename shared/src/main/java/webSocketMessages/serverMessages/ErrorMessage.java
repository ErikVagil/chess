package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;
    private boolean isCriticalError;
    
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    
    public ErrorMessage(String errorMessage, boolean isCriticalError) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
        this.isCriticalError = isCriticalError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isCriticalError() {
        return isCriticalError;
    }
}
