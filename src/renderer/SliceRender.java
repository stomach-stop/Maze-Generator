package renderer;

import game.Cell;
import game.GameSetting;
import game.Maze;
import game.Player;
import game.Position;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** すべての断面に共通する壁・通路・プレイヤーの描画。 */
public class SliceRender implements MazeRender {
    private final Maze maze;
    private final Player player;
    private final SlicePlane plane;
    private final int cellSize = GameSetting.CELL_SIZE;

    public SliceRender(Maze maze, Player player, SlicePlane plane) {
        if (maze.dimensions() != 3) {
            throw new IllegalArgumentException("Slice views require a three-dimensional maze.");
        }
        this.maze = maze;
        this.player = player;
        this.plane = plane;
    }

    @Override
    public final void render(Graphics2D graphics, int width, int height) {
        int columns = maze.size(plane.horizontalAxis());
        int rows = maze.size(plane.verticalAxis());
        Graphics2D g = ViewLayout.fit(graphics, width, height,
                (double) columns * cellSize, (double) rows * cellSize);
        try {
            g.setColor(Color.BLACK);
            Position playerPosition = player.position();
            int[] coordinates = new int[maze.dimensions()];
            coordinates[plane.fixedAxis()] = playerPosition.coordinate(plane.fixedAxis());
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    coordinates[plane.horizontalAxis()] = column;
                    coordinates[plane.verticalAxis()] = row;
                    drawCell(g, maze.cellAt(new Position(coordinates)), column, row);
                }
            }
            g.setColor(playerColor(maze.cellAt(playerPosition)));
            g.fillOval(playerPosition.coordinate(plane.horizontalAxis()) * cellSize + cellSize / 4,
                    playerPosition.coordinate(plane.verticalAxis()) * cellSize + cellSize / 4,
                    cellSize / 2, cellSize / 2);
        } finally {
            g.dispose();
        }
    }

    private void drawCell(Graphics2D g, Cell cell, int column, int row) {
        int x = column * cellSize;
        int y = row * cellSize;
        if (cell.hasWall(plane.right())) g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
        if (cell.hasWall(plane.right().opposite())) g.drawLine(x, y, x, y + cellSize);
        if (cell.hasWall(plane.down())) g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
        if (cell.hasWall(plane.down().opposite())) g.drawLine(x, y, x + cellSize, y);

        boolean positive = !cell.hasWall(plane.forward());
        boolean negative = !cell.hasWall(plane.forward().opposite());
        String arrow = positive && negative ? "↕" : positive ? "↑" : negative ? "↓" : "";
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(arrow, x + (cellSize - metrics.stringWidth(arrow)) / 2 + 1,
                y + (cellSize - metrics.getHeight()) / 2 + metrics.getAscent());
    }

    /** 断面ごとの既存の色分け仕様を、このメソッドで差し替える。 */
    protected Color playerColor(Cell cell) { return Color.BLACK; }
}
