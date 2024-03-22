package ui;

public class Repl {
    protected void printErrorMessage(String errorMessage) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.println(errorMessage);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    protected void printNotification(String notification) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.println(notification);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }
}
