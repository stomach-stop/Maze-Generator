package game;

import java.util.Arrays;

/** 迷路の形状・境界と、隣接セル間の通路の整合性を管理する。 */
public final class Maze {
    private final Cell[] cells;
    private final int[] sizes;
    private boolean generationStarted;

    public Maze(int... sizes) {
        if (sizes == null || sizes.length < 1 || sizes.length > 3) {
            throw new IllegalArgumentException("Maze must have between 1 and 3 dimensions.");
        }
        this.sizes = sizes.clone();
        int count = 1;
        for (int size : sizes) {
            if (size <= 0) throw new IllegalArgumentException("Maze dimensions must be positive.");
            count = Math.multiplyExact(count, size);
        }
        cells = new Cell[count];
        Arrays.setAll(cells, index -> new Cell(sizes.length));
    }

    public int dimensions() { return sizes.length; }
    public int size(int axis) { return sizes[axis]; }
    public int cellCount() { return cells.length; }
    public Cell cellAt(Position position) { return cells[toIndex(position)]; }
    public Cell cellAt(int index) { return cells[index]; }
    public int getDirectionCount() { return dimensions() * 2; }

    public Direction getDirection(int index) {
        if (index < 0 || index >= getDirectionCount()) throw new IndexOutOfBoundsException(index);
        return Direction.values()[index];
    }

    public Direction[] getDirections() {
        return Arrays.copyOf(Direction.values(), getDirectionCount());
    }

    public boolean isInBounds(Position position) {
        if (position == null || position.dimensions() != dimensions()) return false;
        for (int axis = 0; axis < dimensions(); axis++) {
            if (position.coordinate(axis) < 0 || position.coordinate(axis) >= sizes[axis]) return false;
        }
        return true;
    }

    public boolean canVisit(Position position) { return !cellAt(position).isVisited(); }

    public int toIndex(Position position) {
        if (!isInBounds(position)) throw new IllegalArgumentException("Position is outside the maze.");
        int index = 0;
        int stride = 1;
        for (int axis = 0; axis < dimensions(); axis++) {
            index += position.coordinate(axis) * stride;
            stride *= sizes[axis];
        }
        return index;
    }

    public Position positionAt(int index) {
        if (index < 0 || index >= cells.length) throw new IndexOutOfBoundsException(index);
        int[] coordinates = new int[dimensions()];
        for (int axis = 0; axis < dimensions(); axis++) {
            coordinates[axis] = index % sizes[axis];
            index /= sizes[axis];
        }
        return new Position(coordinates);
    }

    public void openPassage(Position from, Direction direction) {
        if (direction.ordinal() >= getDirectionCount()) {
            throw new IllegalArgumentException("Direction is outside the maze dimensions.");
        }
        Position to = from.cloneMoved(direction);
        Cell first = cellAt(from);
        Cell second = cellAt(to); // 両方を検証してから変更する。
        first.removeWall(direction);
        second.removeWall(direction.opposite());
    }

    /** 生成は新しいMazeに一度だけ行う。再生成時は新しいMazeを作る。 */
    public void beginGeneration(Position start) {
        toIndex(start);
        if (generationStarted) throw new IllegalStateException("Create a new Maze to generate again.");
        generationStarted = true;
    }
}
