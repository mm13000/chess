package exception;

public class ResponseException extends Exception {
    final private ResponseException.statusCode statusCode;

    public ResponseException(ResponseException.statusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ResponseException.statusCode StatusCode() {
        return statusCode;
    }

    public enum statusCode {
        // Enum definition. Status code int representation is passed to constructor.
        OK(200),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        TAKEN(403),
        ERROR(500);

        public final int code;
        statusCode(int code) {
            this.code = code;
        }
    }
}
