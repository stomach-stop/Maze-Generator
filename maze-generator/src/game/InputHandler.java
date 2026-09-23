package game;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private GamePanel panel;
    private Player player;
    private Game game;

    public InputHandler(GamePanel panel, Player player, Game game) {
        this.panel = panel;
        this.player = player;
        this.game = game;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            //移動操作
            case KeyEvent.VK_D:
                player.move(Direction.PositiveX);
                break;
            case KeyEvent.VK_A:
                player.move(Direction.NegativeX);
                break;
            case KeyEvent.VK_S:
                player.move(Direction.PositiveY);
                break;
            case KeyEvent.VK_W:
                player.move(Direction.NegativeY);
                break;
            case KeyEvent.VK_E:
                player.move(Direction.PositiveZ);
                break;
            case KeyEvent.VK_Q:
                player.move(Direction.NegativeZ);
                break;

            //画面操作
            case KeyEvent.VK_1:
                panel.setXY();
                break;
            case KeyEvent.VK_2:
                panel.setXZ();
                break;
            case KeyEvent.VK_3:
                panel.setYZ();
                break;
            case KeyEvent.VK_4:
                game.openWindow();
                break;
        }
        panel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}