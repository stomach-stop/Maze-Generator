package renderer;

import game.Maze;
import game.Player;

public final class YZRender extends SliceRender {
    public YZRender(Maze maze, Player player) { super(maze, player, SlicePlane.YZ); }
}
