package request;

import chess.ChessGame;

public record JoinGameNoAuth(ChessGame.TeamColor playerColor, int gameID) {
}
