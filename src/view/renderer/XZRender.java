package view.renderer;

import model.Maze;
import model.Player;

public final class XZRender extends SliceRender {
    public XZRender(Maze maze, Player player) { super(maze, player, SlicePlane.XZ); }
}
