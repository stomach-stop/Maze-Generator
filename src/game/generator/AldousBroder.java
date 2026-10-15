package game.generator;

import java.util.*;
import game.*;

public class AldousBroder implements Generator {
    private final Maze maze;
    private final Random random;

    public AldousBroder(Maze maze) {
        this(maze, new Random());
    }

    public AldousBroder(Maze maze, Random random) {
        this.maze = java.util.Objects.requireNonNull(maze);
        this.random = java.util.Objects.requireNonNull(random);
    }

    @Override
    public void generate(Position pos) {
        maze.beginGeneration(pos);
        Position current = pos;

        int visitedCount = 1;

        // 開始地点を訪問済みにする
        maze.cellAt(current).visit();

        while (visitedCount < maze.cellCount()) {
            // ランダムな方向
            Direction dir =
                maze.getDirection(random.nextInt(maze.getDirectionCount()));

            Position next = current.cloneMoved(dir);

            // 迷路外なら無視
            if (!maze.isInBounds(next)) {
                continue;
            }

            // 未訪問なら壁を壊す
            if (maze.canVisit(next)) {
                maze.openPassage(current, dir);

                maze.cellAt(next).visit();

                visitedCount++;
            }

            // 移動
            current = next;
        }
    }
}
