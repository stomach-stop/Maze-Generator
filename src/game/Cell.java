package game;

import java.util.Arrays;

public final class Cell {
    private boolean visited;
    private final boolean[] walls;

    public Cell(int dimensions) {
        walls = new boolean[dimensions * 2];
        Arrays.fill(walls, true);
    }

    public boolean isVisited() { return visited; }
    public void visit() { visited = true; }
    public boolean hasWall(Direction direction) { return walls[direction.ordinal()]; }

    // 壁の変更は同じパッケージのMazeから行い、隣接セルとの対称性を守る。
    void removeWall(Direction direction) { walls[direction.ordinal()] = false; }
}
