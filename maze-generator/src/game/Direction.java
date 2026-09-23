package game;

public enum Direction {
    PositiveX(1, 0, 0, 0),
    NegativeX(-1, 0, 0, 0),
    PositiveY(0, 1, 0, 0),
    NegativeY(0, -1, 0, 0),
    PositiveZ(0, 0, 1, 0),
    NegativeZ(0, 0, -1, 0),
    PositiveW(0, 0, 0, 1),
    NegativeW(0, 0, 0, -1);

    private int[] delta;

    Direction(int... delta) {
        this.delta = delta;
    }

    public int[] get() {
        return delta;
    }

    public Direction opposite() {
        switch (this) {
            case PositiveX: return NegativeX;
            case NegativeX: return PositiveX;
            case PositiveY: return NegativeY;
            case NegativeY: return PositiveY;
            case PositiveZ: return NegativeZ;
            case NegativeZ: return PositiveZ;
            case PositiveW: return NegativeW;
            case NegativeW: return PositiveW;
            default: throw new IllegalArgumentException("Invalid direction: " + this);
        }
    }
}