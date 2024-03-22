package ui;

import static ui.EscapeSequences.*;

public class Repl {
    protected void printErrorMessage(String errorMessage) {
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(errorMessage);
        System.out.print(RESET_TEXT_COLOR);
    }

    protected void printNotification(String notification) {
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println(notification);
        System.out.print(RESET_TEXT_COLOR);
    }
}
