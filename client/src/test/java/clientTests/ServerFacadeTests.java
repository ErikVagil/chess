package clientTests;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import dataAccess.*;
import model.*;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static DAO dao;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
        dao = new QueryDAO();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void prepDB() {
        try {
            dao.clear();
        } catch (Exception e) {}
    }

    @Test
    public void testClientLogin() {
        try {
            dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        } catch (Exception e) {}

        ArrayList<AuthData> results = new ArrayList<>();

        assertDoesNotThrow(() -> {
            results.add(facade.clientLogin("testuser", "testpass"));
        });

        assertNotNull(results.get(0));
    }

    @Test
    public void testClientRegister() {
        ArrayList<AuthData> results = new ArrayList<>();

        assertDoesNotThrow(() -> {
            results.add(facade.clientRegister("testuser", "testpass", "testmail@mail.com"));
        });

        assertNotNull(results.get(0));
    }

    @Test
    public void testClientLogout() {
        ArrayList<String> results = new ArrayList<>();
        assertDoesNotThrow(() -> dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com")));
        assertDoesNotThrow(() -> results.add(dao.createAuth("testuser")));
        assertDoesNotThrow(() -> facade.clientLogout(results.get(0)));
        assertDoesNotThrow(() -> dao.getUser("testuser"));
    }

    @Test
    public void testClientCreate() {
        int listSize;
        ArrayList<String> params = new ArrayList<>();
        try {
            listSize = dao.listGames().size();
            assertEquals(listSize, 0);
            dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
            params.add(dao.createAuth("testuser"));
        } catch (Exception e) {}
        assertDoesNotThrow(() -> facade.clientCreate(params.get(0), "testgame"));
        try {
            listSize = dao.listGames().size();
            assertNotEquals(listSize, 0);
        } catch (Exception e) {}
    }

    @Test
    public void testClientList() {
        ArrayList<String> params = new ArrayList<>();
        try {
            dao.createGame(new GameData(101, null, null, "mario", null));
            dao.createGame(new GameData(102, null, null, "luigi", null));
            dao.createGame(new GameData(103, null, null, "peach", null));
            dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
            params.add(dao.createAuth("testuser"));
        } catch (Exception e) {}
        ArrayList<Integer> results = new ArrayList<>();
        assertDoesNotThrow(() -> results.add(facade.clientList(params.get(0)).size()));
        assertEquals(results.get(0), 3);
    }

    @Test
    public void testClientJoin() {
        ArrayList<String> params = new ArrayList<>();
        try {
            dao.createGame(new GameData(101, null, null, "mario", null));
            dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
            params.add(dao.createAuth("testuser"));
        } catch (Exception e) {}
        assertDoesNotThrow(() -> facade.clientJoin(params.get(0), 101, "WHITE"));
        ArrayList<String> results = new ArrayList<>();
        try {
            results.add(dao.getGame(101).whiteUsername);
        } catch (Exception e) {}
        assertNotNull(results.get(0));
    }
}