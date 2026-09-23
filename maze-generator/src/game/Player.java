package game;

public class Player {
    public Position pos;
    private Maze maze;
    
    public Player(Maze maze) {
        pos = new Position(
            GameSetting.START_POSITION
        );
        this.maze = maze;
    }

    public void move(Direction dir) {
        Position next = pos.cloneMoved(dir);
        if (
            maze.isInBounds(next) &&
            !maze.cells[maze.toIndex(pos)].hasWall(dir)
        ) {
            pos = next;
        }
    }
}