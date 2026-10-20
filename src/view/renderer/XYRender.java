package view.renderer;

import java.awt.Color;
import java.util.function.IntSupplier;
import model.Cell;
import model.Direction;
import model.Maze;
import model.Player;

/** XY断面。Z方向の通路に応じた従来の色分けを維持する。 */
public final class XYRender extends SliceRender {
    public XYRender(Maze maze, Player player) { super(maze, player, SlicePlane.XY); }

    public XYRender(Maze maze, Player player, IntSupplier displayedLayer) {
        super(maze, player, SlicePlane.XY, displayedLayer);
    }

    @Override
    protected Color playerColor(Cell cell) {
        boolean positive = !cell.hasWall(Direction.PositiveZ);
        boolean negative = !cell.hasWall(Direction.NegativeZ);
        return positive && negative ? Color.MAGENTA
                : positive ? Color.RED : negative ? Color.BLUE : Color.BLACK;
    }
}
