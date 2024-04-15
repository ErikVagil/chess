package ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import chess.*;
import chess.ChessGame.TeamColor;
import model.*;

public class FacadeFactory {
    public static void renderChessBoardWhite(ChessGame game, ChessPosition legalMovesPos) {
        ChessBoard board = game.getBoard();
        Collection<ChessPosition> legalMoves;
        if (legalMovesPos == null) {
            legalMoves = new HashSet<>();
        } else {
            if (board.getPiece(legalMovesPos) == null) {
                System.out.println("Please choose a valid piece to show moves for.");
                return;
            }
            legalMoves = getEndPositionsFromList(game.validMoves(legalMovesPos));
        }
        System.out.println();
        for (int row = 8; row >= 1; row--) {
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + row + "  ");
            for (int col = 1; col <= 8; col++) {
                printTileBackground(row, col, legalMoves);

                ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
                String currentSymbol = getPieceSymbol(currentPiece);
                System.out.print(currentSymbol);
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            System.out.println();
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println(EscapeSequences.EMPTY + " a  b  c  d  e  f  g  h \n");
    }

    public static void renderChessBoardBlack(ChessGame game, ChessPosition legalMovesPos) {
        ChessBoard board = game.getBoard();
        Collection<ChessPosition> legalMoves;
        if (legalMovesPos == null) {
            legalMoves = new HashSet<>();
        } else {
            if (board.getPiece(legalMovesPos) == null) {
                System.out.println("Please choose a valid piece to show moves for.");
                return;
            }
            legalMoves = getEndPositionsFromList(game.validMoves(legalMovesPos));
        }
        System.out.println();
        for (int row = 1; row <= 8; row++) {
            System.out.print(EscapeSequences.RESET_BG_COLOR + row + "  ");
            for (int col = 8; col >= 1; col--) {
                printTileBackground(row, col, legalMoves);

                ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
                String currentSymbol = getPieceSymbol(currentPiece);
                System.out.print(currentSymbol);
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            System.out.println();
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println(EscapeSequences.EMPTY + " h  g  f  e  d  c  b  a \n");
    }

    private static Collection<ChessPosition> getEndPositionsFromList(Collection<ChessMove> moves) {
        Collection<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    private static void printTileBackground(int row, int col, Collection<ChessPosition> legalMoves) {
        if ((row + col) % 2 == 0) {
            if (legalMoves.contains(new ChessPosition(row, col))) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            }
        } else {
            if (legalMoves.contains(new ChessPosition(row, col))) {
                System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
            }
        }
    }

    public static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;

        if (piece.getTeamColor() == TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KING;
                case QUEEN:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_QUEEN;
                case BISHOP:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_BISHOP;
                case KNIGHT:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KNIGHT;
                case ROOK:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_ROOK;
                case PAWN:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_PAWN;
                default:
                    return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.EMPTY;
            }
        } else {
            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KING;
                case QUEEN:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_QUEEN;
                case BISHOP:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_BISHOP;
                case KNIGHT:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KNIGHT;
                case ROOK:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_ROOK;
                case PAWN:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_PAWN;
                default:
                    return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.EMPTY;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static AuthData clientLogin(String username, String password, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        Map<String, String> reqBody = Map.of("username", username, "password", password);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Map<String, String> res = new Gson().fromJson(inputStreamReader, Map.class);
            return new AuthData(res.get("authToken"), res.get("username"));
        }
    }

    public static AuthData clientRegister(String username, String password, String email, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/user");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        UserData reqBody = new UserData(username, password, email);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody, UserData.class);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new Gson().fromJson(inputStreamReader, model.AuthData.class);
        }
    }

    public static int clientLogout(String authToken, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        http.connect();

        return http.getResponseCode();
    }

    @SuppressWarnings("unchecked")
    public static int clientCreate(String authToken, String gameName, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        Map<String, String> reqBody = Map.of("gameName", gameName);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Map<String, Double> resBody = new Gson().fromJson(inputStreamReader, Map.class);
            int gameID = (int) Math.round(resBody.get("gameID"));
            return gameID;
        }
    }

    public static Collection<GameData> clientList(String authToken, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        http.connect();

        try (InputStream inputStream = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Type collectionType = new TypeToken<Map<String, Collection<GameData>>>(){}.getType();
            Map<String, Collection<GameData>> resBody = new Gson().fromJson(inputStreamReader, collectionType);
            return resBody.get("games");
        }
    }

    public static int clientJoin(String authToken, int gameID, String playerColor, int port) throws Exception {
        URI uri = new URI("http://localhost:" + port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("PUT");

        http.setDoOutput(true);

        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("Authorization", authToken);

        if (playerColor == null) playerColor = "null";
        Map<String, Object> reqBody = Map.of("playerColor", playerColor, "gameID", gameID);
        try (OutputStream outputStream = http.getOutputStream()) {
            String jsonBody = new Gson().toJson(reqBody);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        return http.getResponseCode();
    }
}
