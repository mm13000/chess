package exception;

import status.StatusCode;

public class ResponseException extends Exception {
    final private StatusCode statusCode;

    public ResponseException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCode StatusCode() {
        return statusCode;
    }
}
