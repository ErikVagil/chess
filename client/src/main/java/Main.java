import server.Server;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);
        ServerFacade client = new ServerFacade();
        client.run();
    }
}