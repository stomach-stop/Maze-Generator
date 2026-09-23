package renderer;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import game.*;

public class XYRender implements MazeRender {
    private game.Maze maze;
    private game.Player player;
    private int cellSize;

    public XYRender(game.Maze maze, game.Player player) {
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
            coords[2] = player.pos.coords[2];
            Position pos = new Position(coords);
            Cell cell = maze.cells[maze.toIndex(pos)];
            drawCell(g, cell, coords);
            return;
        }

        for (int i = 0; i < maze.mazeSize[dimension]; i++) {
            coords[dimension] = i;
            drawCells(g, coords, dimension + 1);
        }
    }

    @Override
    public void drawCell(Graphics g, Cell cell, int[] coords) {
        int x = coords[0] * cellSize;
        int y = coords[1] * cellSize;

        if (cell.hasWall(Direction.PositiveX)) {
            g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.NegativeX)) {
            g.drawLine(x, y, x, y + cellSize);
        }
        if (cell.hasWall(Direction.PositiveY)) {
            g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.NegativeY)) {
            g.drawLine(x, y, x + cellSize, y);
        }

        FontMetrics fm = g.getFontMetrics();

        int textX = x + (cellSize - fm.stringWidth("↑")) / 2 + 1;
        int textY = y + (cellSize - fm.getHeight()) / 2 + fm.getAscent();

        if (!cell.hasWall(Direction.PositiveZ)) {
            g.drawString("↑", textX, textY);
        }

        if (!cell.hasWall(Direction.NegativeZ)) {
            g.drawString("↓", textX, textY);
        }
    }

    @Override
    public void drawPlayer(Graphics g) {
        Cell cell = maze.cells[maze.toIndex(player.pos)];

        boolean canMoveUp = !cell.hasWall(Direction.PositiveZ);
        boolean canMoveDown = !cell.hasWall(Direction.NegativeZ);

        Color color;

        if (canMoveUp && canMoveDown) {
            color = Color.MAGENTA;
        } else if (canMoveUp) {
            color = Color.RED;
        } else if (canMoveDown) {
            color = Color.BLUE;
        } else {
            color = Color.BLACK;
        }

        g.setColor(color);

        g.fillOval(
            player.pos.coords[0] * cellSize + cellSize / 4,
            player.pos.coords[1] * cellSize + cellSize / 4,
            cellSize / 2,
            cellSize / 2
        );
    }
}