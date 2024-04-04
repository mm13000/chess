package exception;

import org.jetbrains.annotations.NotNull;

public class ResponseException extends Exception {
    final private StatusCode statusCode;

    public ResponseException(@NotNull StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCode StatusCode() {
        return statusCode;
    }

    public enum StatusCode {
        // Enum definition. Status code int representation is passed to constructor.
        OK(200),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        TAKEN(403),
        ERROR(500);

        public final int code;
        StatusCode(int code) {
            this.code = code;
        }
    }
}
