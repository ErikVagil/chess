package clientTests;

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
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
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
    public void testClientLoginPos() throws Exception {
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));

        AuthData result = facade.clientLogin("testuser", "testpass");

        assertNotNull(result);
    }

    @Test
    public void testClientLoginNeg() throws Exception {
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));

        assertThrows(Exception.class, () -> facade.clientLogin("testuser", "wrongpassword"));
    }

    @Test
    public void testClientRegisterPos() throws Exception {
        AuthData result = facade.clientRegister("testuser", "testpass", "testmail@mail.com");

        assertNotNull(result);
    }

    @Test
    public void testClientRegisterNeg() throws Exception {
        assertThrows(Exception.class, () -> facade.clientRegister(null, null, null));
    }

    @Test
    public void testClientLogoutPos() throws Exception {
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        facade.clientLogout(token);
        assertNull(dao.getAuth(token));
    }

    @Test
    public void testClientLogoutNeg() throws Exception {
        int httpcode = facade.clientLogout(null);
        assertNotEquals(httpcode, 200);
    }

    @Test
    public void testClientCreatePos() throws Exception {
        int listSize;
        listSize = dao.listGames().size();
        assertEquals(listSize, 0);
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        facade.clientCreate(token, "testgame");
        listSize = dao.listGames().size();
        assertNotEquals(listSize, 0);
    }

    @Test
    public void testClientCreateNeg() throws Exception {
        assertThrows(Exception.class, () -> facade.clientCreate("badauth", "testgame"));
    }

    @Test
    public void testClientListPos() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        dao.createGame(new GameData(102, null, null, "luigi", null));
        dao.createGame(new GameData(103, null, null, "peach", null));
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        int result = facade.clientList(token).size();
        assertEquals(result, 3);
    }

    @Test
    public void testClientListNeg() throws Exception {
        assertThrows(Exception.class, () -> facade.clientList("badauth"));
    }

    @Test
    public void testClientJoinPos() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        assertDoesNotThrow(() -> facade.clientJoin(token, 101, "WHITE"));
        String whiteUsername = dao.getGame(101).whiteUsername;
        assertNotNull(whiteUsername);
    }

    @Test
    public void testClientJoinNeg() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        int httpcode = facade.clientJoin("badauth", 101, "WHITE");
        assertNotEquals(httpcode, 200);
    }
}