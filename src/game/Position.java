package game;

import java.util.Arrays;

/** 生成後に値が変わらない座標。配列を外部と共有しない。 */
public final class Position {
    private final int[] coordinates;

    public Position(int... values) { coordinates = values.clone(); }
    public int dimensions() { return coordinates.length; }
    public int coordinate(int axis) { return coordinates[axis]; }
    public int[] toArray() { return coordinates.clone(); }

    public Position cloneMoved(Direction direction) {
        int[] moved = coordinates.clone();
        int[] delta = direction.get();
        for (int axis = 0; axis < moved.length; axis++) moved[axis] += delta[axis];
        return new Position(moved);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Position
                && Arrays.equals(coordinates, ((Position) other).coordinates);
    }

    @Override
    public int hashCode() { return Arrays.hashCode(coordinates); }
}
