package clientTests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import dataAccess.*;
import model.*;
import server.Server;
import ui.FacadeFactory;


public class ServerFacadeTests {

    private static Server server;
    private static DAO dao;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
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

        AuthData result = FacadeFactory.clientLogin("testuser", "testpass", port);

        assertNotNull(result);
    }

    @Test
    public void testClientLoginNeg() throws Exception {
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));

        assertThrows(Exception.class, () -> FacadeFactory.clientLogin("testuser", "wrongpassword", port));
    }

    @Test
    public void testClientRegisterPos() throws Exception {
        AuthData result = FacadeFactory.clientRegister("testuser", "testpass", "testmail@mail.com", port);

        assertNotNull(result);
    }

    @Test
    public void testClientRegisterNeg() throws Exception {
        assertThrows(Exception.class, () -> FacadeFactory.clientRegister(null, null, null, port));
    }

    @Test
    public void testClientLogoutPos() throws Exception {
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        FacadeFactory.clientLogout(token, port);
        assertNull(dao.getAuth(token));
    }

    @Test
    public void testClientLogoutNeg() throws Exception {
        int httpcode = FacadeFactory.clientLogout(null, port);
        assertNotEquals(httpcode, 200);
    }

    @Test
    public void testClientCreatePos() throws Exception {
        int listSize;
        listSize = dao.listGames().size();
        assertEquals(listSize, 0);
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        FacadeFactory.clientCreate(token, "testgame", port);
        listSize = dao.listGames().size();
        assertNotEquals(listSize, 0);
    }

    @Test
    public void testClientCreateNeg() throws Exception {
        assertThrows(Exception.class, () -> FacadeFactory.clientCreate("badauth", "testgame", port));
    }

    @Test
    public void testClientListPos() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        dao.createGame(new GameData(102, null, null, "luigi", null));
        dao.createGame(new GameData(103, null, null, "peach", null));
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        int result = FacadeFactory.clientList(token, port).size();
        assertEquals(result, 3);
    }

    @Test
    public void testClientListNeg() throws Exception {
        assertThrows(Exception.class, () -> FacadeFactory.clientList("badauth", port));
    }

    @Test
    public void testClientJoinPos() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        dao.createUser(new UserData("testuser", "testpass", "testmail@mail.com"));
        String token = dao.createAuth("testuser");
        assertDoesNotThrow(() -> FacadeFactory.clientJoin(token, 101, "WHITE", port));
        String whiteUsername = dao.getGame(101).whiteUsername;
        assertNotNull(whiteUsername);
    }

    @Test
    public void testClientJoinNeg() throws Exception {
        dao.createGame(new GameData(101, null, null, "mario", null));
        int httpcode = FacadeFactory.clientJoin("badauth", 101, "WHITE", port);
        assertNotEquals(httpcode, 200);
    }
}