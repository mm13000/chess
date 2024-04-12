import ui.PreloginUI;

public class Client {
    public static void main(String[] args) {
        int serverPort = 8080;
        String serverDomain = "http://localhost";
        PreloginUI preloginUI = new PreloginUI(serverDomain, serverPort);
        preloginUI.run();
    }
}