package renderer;

import game.Maze;
import game.Player;

public final class XZRender extends SliceRender {
    public XZRender(Maze maze, Player player) { super(maze, player, SlicePlane.XZ); }
}
