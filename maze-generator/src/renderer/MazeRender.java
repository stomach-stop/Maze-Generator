package renderer;
import java.awt.Graphics;
import game.Cell;

public interface MazeRender {
    void drawMaze(Graphics g);
    void drawCells(Graphics g, int[] coords, int dimension);
    void drawCell(Graphics g, Cell cell, int[] coords);
    void drawPlayer(Graphics g);
}