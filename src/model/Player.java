package model;

import java.util.HashSet;
import java.util.Set;

/** プレイヤーの位置と移動規則。画面設定に依存しない。 */
public final class Player {
    private final Maze maze;
    private final Position startPosition;
    private Position position;
    private long moveCount;

    public long moveCount() { return moveCount; }
    // 通ったセルの組を記録する。往復は同じ通路として扱う。
    private final Set<Long> traversedPassages = new HashSet<>();
    private boolean trailVisible = false;

    public boolean isTrailVisible() { return trailVisible; }
    public void toggleTrailVisibility() { trailVisible = !trailVisible; }

    public boolean hasTraversed(Position from, Position to) {
        return maze.isInBounds(from) && maze.isInBounds(to)
                && traversedPassages.contains(passageKey(from, to));
    }

    private long passageKey(Position from, Position to) {
        int a = maze.toIndex(from);
        int b = maze.toIndex(to);
        return ((long) Math.min(a, b) << 32) | (Math.max(a, b) & 0xffffffffL);
    }

    public Player(Maze maze, Position startPosition) {
        if (!maze.isInBounds(startPosition)) throw new IllegalArgumentException("Invalid start position.");
        this.maze = maze;
        this.startPosition = startPosition;
        position = startPosition;
    }

    public boolean hasReachedGoal() { return position.equals(maze.goalPosition()); }

    public Position position() { return position; }
    public void resetPosition() { position = startPosition; }

    public void move(Direction direction) {
        if (direction.ordinal() >= maze.getDirectionCount()) return;
        Position next = position.cloneMoved(direction);
        if (maze.isInBounds(next) && !maze.cellAt(position).hasWall(direction)) {
            traversedPassages.add(passageKey(position, next));
            position = next;
            moveCount++;
        }
    }
}
