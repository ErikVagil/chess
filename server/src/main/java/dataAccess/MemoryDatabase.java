package dataAccess;

import java.util.ArrayList;

import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDatabase {
    public static ArrayList<UserData> userDB = new ArrayList<>();
    public static ArrayList<GameData> gameDB = new ArrayList<>();
    public static ArrayList<AuthData> authDB = new ArrayList<>();
}
