package game.generator;

import java.util.*;
import game.*;

public class Wilson implements Generator {
    private final Maze maze;
    private final Random random;

    public Wilson(Maze maze) {
        this(maze, new Random());
    }

    public Wilson(Maze maze, Random random) {
        this.maze = java.util.Objects.requireNonNull(maze);
        this.random = java.util.Objects.requireNonNull(random);
    }

    @Override
    public void generate(Position pos) {
        maze.beginGeneration(pos);
        // 最初のセルを訪問済みにする
        int startIndex = maze.toIndex(pos);
        maze.cellAt(startIndex).visit();

        int visitedCount = 1;

        while (visitedCount < maze.cellCount()) {

            // 未訪問セルをランダムに選ぶ
            Position current = randomUnvisitedPosition();

            List<Position> path = new ArrayList<>();
            path.add(current);

            // ランダムウォーク
            while (!isVisited(current)) {

                Direction dir =
                    maze.getDirection(random.nextInt(maze.getDirectionCount()));

                Position next = current.cloneMoved(dir);

                // 迷路外ならやり直す
                if (!maze.isInBounds(next)) {
                    continue;
                }

                current = next;

                // 訪問済みセルに到達したら終了
                if (isVisited(current)) {
                    break;
                }

                // ループを削除、または経路に追加
                if (!eraseLoop(path, current)) {
                    path.add(current);
                }
            }

            // pathを迷路に追加
            for (int i = 0; i < path.size(); i++) {
                Position cell = path.get(i);

                Position next;

                if (i + 1 < path.size()) {
                    next = path.get(i + 1);
                } else {
                    next = current;
                }

                Direction dir = getDirection(cell, next);

                // 壁を壊す
                maze.openPassage(cell, dir);

                // 訪問済みにする
                if (!isVisited(cell)) {
                    maze.cellAt(cell).visit();
                    visitedCount++;
                }
            }
        }
    }

    // 未訪問セルをランダムに取得
    private Position randomUnvisitedPosition() {
        while (true) {
            int index = random.nextInt(maze.cellCount());

            Position pos = maze.positionAt(index);

            if (!isVisited(pos)) {
                return pos;
            }
        }
    }

    // 指定したセルが訪問済みか
    private boolean isVisited(Position pos) {
        return !maze.canVisit(pos);
    }

    // path内のループを削除
    private boolean eraseLoop(List<Position> path, Position current) {
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).equals(current)) {
                path.subList(i + 1, path.size()).clear();
                return true;
            }
        }

        return false;
    }

    // 2つのPositionから方向を取得
    private Direction getDirection(Position from, Position to) {
        for (Direction dir : maze.getDirections()) {
            Position next = from.cloneMoved(dir);

            if (next.equals(to)) {
                return dir;
            }
        }

        throw new IllegalStateException("Path contains non-adjacent cells.");
    }

}
