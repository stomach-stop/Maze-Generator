package game;

/** プレイヤーの位置と移動規則。画面設定に依存しない。 */
public final class Player {
    private final Maze maze;
    private final Position startPosition;
    private Position position;

    public Player(Maze maze, Position startPosition) {
        if (!maze.isInBounds(startPosition)) throw new IllegalArgumentException("Invalid start position.");
        this.maze = maze;
        this.startPosition = startPosition;
        position = startPosition;
    }

    public Position position() { return position; }
    public void resetPosition() { position = startPosition; }

    public void move(Direction direction) {
        if (direction.ordinal() >= maze.getDirectionCount()) return;
        Position next = position.cloneMoved(direction);
        if (maze.isInBounds(next) && !maze.cellAt(position).hasWall(direction)) position = next;
    }
}
