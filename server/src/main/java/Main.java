import dataAccess.DataAccessException;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);

//        Server server = new Server();
//        int port = server.run(8080);
//        System.out.println("Server started on port: " + port);

        AuthDAO authDAO = new AuthDAOmySQL();
        UserDAO userDAO = new UserDAOmySQL();
        GameDAO gameDAO = new GameDAOmySQL();
        try {
            gameDAO.clearGames();
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
//        AuthService authService = new AuthService(authDAO);
//        UserService
    }
}