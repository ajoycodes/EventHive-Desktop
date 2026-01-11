package app;

/**
 * Launcher class to workaround "JavaFX runtime components are missing" error
 */
public class Launcher {
    public static void main(String[] args) {
        System.out.println("[Launcher] Starting application...");
        Main.main(args);
    }
}
