import game.Game;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        javax.swing.SwingUtilities.invokeLater(Game::new);
    }
}