package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    protected static String getCommand() {
        // Get user command from the terminal
        System.out.print("\nEnter command: ");
        return new Scanner(System.in).next().toLowerCase();
    }

    protected void printErrorMessage(String errorMessage) {
        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_BOLD);
        System.out.println(errorMessage);
        System.out.print(RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
    }

    protected void printNotification(String notification) {
        System.out.print(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD);
        System.out.println(notification);
        System.out.print(RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
    }
}
