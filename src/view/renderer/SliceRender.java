package view.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.function.IntSupplier;
import model.Cell;
import model.Direction;
import model.Maze;
import model.Player;
import model.Position;
import view.ViewLayout;
import view.ViewSettings;

/** すべての断面に共通する壁・通路・プレイヤーの描画。 */
public class SliceRender implements MazeRender {
    private final Maze maze;
    private final Player player;
    private final SlicePlane plane;
    private final IntSupplier displayedLayer;
    private final int cellSize = ViewSettings.CELL_SIZE;

    public SliceRender(Maze maze, Player player, SlicePlane plane) {
        this(maze, player, plane, () -> player.position().coordinate(plane.fixedAxis()));
    }

    public SliceRender(Maze maze, Player player, SlicePlane plane, IntSupplier displayedLayer) {
        if (maze.dimensions() != 3) {
            throw new IllegalArgumentException("Slice views require a three-dimensional maze.");
        }
        this.maze = maze;
        this.player = player;
        this.plane = plane;
        this.displayedLayer = displayedLayer;
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
            int layer = displayedLayer.getAsInt();
            coordinates[plane.fixedAxis()] = layer;
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    coordinates[plane.horizontalAxis()] = column;
                    coordinates[plane.verticalAxis()] = row;
                    drawCell(g, maze.cellAt(new Position(coordinates)), column, row);
                }
            }
            if (player.isTrailVisible()) drawTrail(g, coordinates, columns, rows);
            Position goal = maze.goalPosition();
            if (layer == goal.coordinate(plane.fixedAxis())) {
                int x = goal.coordinate(plane.horizontalAxis()) * cellSize;
                int y = goal.coordinate(plane.verticalAxis()) * cellSize;
                g.setColor(new Color(20, 145, 75));
                g.fillRect(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
                g.setColor(Color.WHITE);
                FontMetrics metrics = g.getFontMetrics();
                g.drawString("G", x + (cellSize - metrics.stringWidth("G")) / 2,
                        y + (cellSize - metrics.getHeight()) / 2 + metrics.getAscent());
            }
            if (layer == playerPosition.coordinate(plane.fixedAxis())) {
                g.setColor(playerColor(maze.cellAt(playerPosition)));
                g.fillOval(playerPosition.coordinate(plane.horizontalAxis()) * cellSize + cellSize / 4,
                        playerPosition.coordinate(plane.verticalAxis()) * cellSize + cellSize / 4,
                        cellSize / 2, cellSize / 2);
            }
        } finally {
            g.dispose();
        }
    }

    /** 今表示している断面内の通過済み通路だけを、セル中心同士で結ぶ。 */
    private void drawTrail(Graphics2D graphics, int[] coordinates, int columns, int rows) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setColor(new Color(210, 40, 40));
            g.setStroke(new BasicStroke(cellSize * 0.10f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Direction[] directions = { plane.right(), plane.down() };
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    coordinates[plane.horizontalAxis()] = column;
                    coordinates[plane.verticalAxis()] = row;
                    Position from = new Position(coordinates);
                    for (Direction direction : directions) {
                        Position to = from.cloneMoved(direction);
                        if (player.hasTraversed(from, to)) {
                            g.draw(new Line2D.Double((column + 0.5) * cellSize,
                                    (row + 0.5) * cellSize,
                                    (to.coordinate(plane.horizontalAxis()) + 0.5) * cellSize,
                                    (to.coordinate(plane.verticalAxis()) + 0.5) * cellSize));
                        }
                    }
                }
            }
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
