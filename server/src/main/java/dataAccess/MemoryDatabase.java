package dataAccess;

import java.util.ArrayList;

import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDatabase {
    public static ArrayList<UserData> userDB;
    public static ArrayList<GameData> gameDB;
    public static ArrayList<AuthData> authDB;
}
