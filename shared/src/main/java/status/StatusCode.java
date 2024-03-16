package status;

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
