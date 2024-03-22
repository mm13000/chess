import ui.PreloginRepl;

public class Main {
    public static void main(String[] args) {
        int serverPort = 8080;
        String serverDomain = "http://localhost";
        PreloginRepl preloginRepl = new PreloginRepl(serverDomain, serverPort);
        preloginRepl.run();
    }
}