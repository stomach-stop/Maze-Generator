package game;
import javax.swing.JPanel;
import java.awt.Graphics;
import renderer.*;

public class GamePanel extends JPanel {
    private Maze maze;
    private Player player;
    private MazeRender render;

    public GamePanel(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
        this.render = new XYRender(maze, player);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render.drawMaze(g);
        render.drawPlayer(g);
    }

    public void setXY() {
        this.render = new XYRender(maze, player);
    }

    public void setXZ() {
        this.render = new XZRender(maze, player);
    }

    public void setYZ() {
        this.render = new YZRender(maze, player);
    }
}