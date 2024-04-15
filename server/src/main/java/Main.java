import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Server running on port " + server.run(8080));
    }
}