package game.generator;

import java.util.*;
import game.*;

public class AldousBroder implements Generator {
    private Maze maze;
    private Random random = new Random();

    public AldousBroder(Maze maze) {
        this.maze = maze;
        System.out.println("AldousBroder");
    }

    @Override
    public void generate(Position pos) {
        Position current = new Position(pos.coords);

        int visitedCount = 1;

        // 開始地点を訪問済みにする
        maze.cells[maze.toIndex(current)].visit();

        while (visitedCount < maze.cells.length) {
            // ランダムな方向
            Direction dir =
                Direction.values()[random.nextInt(Direction.values().length)];

            Position next = current.cloneMoved(dir);

            // 迷路外なら無視
            if (!maze.isInBounds(next)) {
                continue;
            }

            int currentIndex = maze.toIndex(current);
            int nextIndex = maze.toIndex(next);

            // 未訪問なら壁を壊す
            if (maze.canVisit(next)) {
                maze.cells[currentIndex].removeWall(dir);
                maze.cells[nextIndex].removeWall(dir.opposite());

                maze.cells[nextIndex].visit();

                visitedCount++;
            }

            // 移動
            current = next;
        }
    }
}