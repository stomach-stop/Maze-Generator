package renderer;

import java.awt.FontMetrics;
import java.awt.Graphics;
import game.*;

public class XZRender implements MazeRender {
    private game.Maze maze;
    private game.Player player;
    private int cellSize;

    public XZRender(game.Maze maze, game.Player player) {
        this.maze = maze;
        this.player = player;
        this.cellSize = game.GameSetting.CELL_SIZE;
    }

    @Override
    public void drawMaze(Graphics g) {
        int[] coords = new int[maze.mazeSize.length];
        drawCells(g, coords, 0);
    }

    @Override
    public void drawCells(Graphics g, int[] coords, int dimension) {
        if (dimension == 2) {
            coords[1] = player.pos.coords[1];
            Position pos = new Position(coords);
            Cell cell = maze.cells[maze.toIndex(pos)];
            drawCell(g, cell, coords);
            return;
        }

        int axis = dimension == 0 ? 0 : 2;

        for (int i = 0; i < maze.mazeSize[axis]; i++) {
            coords[axis] = i;
            drawCells(g, coords, dimension + 1);
        }
    }

    @Override
    public void drawCell(Graphics g, Cell cell, int[] coords) {
        int x = coords[0] * cellSize;
        int y = coords[2] * cellSize;

        if (cell.hasWall(Direction.PositiveX)) {
            g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.NegativeX)) {
            g.drawLine(x, y, x, y + cellSize);
        }
        if (cell.hasWall(Direction.PositiveZ)) {
            g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.NegativeZ)) {
            g.drawLine(x, y, x + cellSize, y);
        }

        FontMetrics fm = g.getFontMetrics();

        int textX = x + (cellSize - fm.stringWidth("↑")) / 2 + 1;
        int textY = y + (cellSize - fm.getHeight()) / 2 + fm.getAscent();

        if (!cell.hasWall(Direction.PositiveX)) {
            g.drawString("↑", textX, textY);
        }

        if (!cell.hasWall(Direction.NegativeX)) {
            g.drawString("↓", textX, textY);
        }
    }

    @Override
    public void drawPlayer(Graphics g) {
        g.fillOval(
            player.pos.coords[0] * cellSize + cellSize / 4,
            player.pos.coords[2] * cellSize + cellSize / 4,
            cellSize / 2,
            cellSize / 2
        );
    }
}