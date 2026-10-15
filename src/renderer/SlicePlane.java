package renderer;

import game.Direction;

/** 断面の横軸・縦軸・固定する軸を定義する。 */
public enum SlicePlane {
    XY(0, 1, 2, Direction.PositiveX, Direction.PositiveY, Direction.PositiveZ),
    XZ(0, 2, 1, Direction.PositiveX, Direction.PositiveZ, Direction.PositiveY),
    YZ(1, 2, 0, Direction.PositiveY, Direction.PositiveZ, Direction.PositiveX);

    private final int horizontalAxis;
    private final int verticalAxis;
    private final int fixedAxis;
    private final Direction right;
    private final Direction down;
    private final Direction forward;

    SlicePlane(int horizontalAxis, int verticalAxis, int fixedAxis,
               Direction right, Direction down, Direction forward) {
        this.horizontalAxis = horizontalAxis;
        this.verticalAxis = verticalAxis;
        this.fixedAxis = fixedAxis;
        this.right = right;
        this.down = down;
        this.forward = forward;
    }

    public int horizontalAxis() { return horizontalAxis; }
    public int verticalAxis() { return verticalAxis; }
    public int fixedAxis() { return fixedAxis; }
    public Direction right() { return right; }
    public Direction down() { return down; }
    public Direction forward() { return forward; }
}
