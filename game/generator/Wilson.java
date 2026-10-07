package game.generator;

import java.util.*;
import game.*;

public class Wilson implements Generator {
    private Maze maze;
    private Random random = new Random();

    public Wilson(Maze maze) {
        this.maze = maze;
        System.out.println("Wilson");
    }

    @Override
    public void generate(Position pos) {
        // 最初のセルを訪問済みにする
        int startIndex = maze.toIndex(pos);
        maze.cells[startIndex].visit();

        int visitedCount = 1;

        while (visitedCount < maze.cells.length) {

            // 未訪問セルをランダムに選ぶ
            Position current = randomUnvisitedPosition();

            List<Position> path = new ArrayList<>();
            path.add(new Position(current.coords));

            // ランダムウォーク
            while (!isVisited(current)) {

                Direction dir =
                    Direction.values()[random.nextInt(Direction.values().length)];

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
                    path.add(new Position(current.coords));
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

                int cellIndex = maze.toIndex(cell);
                int nextIndex = maze.toIndex(next);

                // 壁を壊す
                maze.cells[cellIndex].removeWall(dir);
                maze.cells[nextIndex].removeWall(dir.opposite());

                // 訪問済みにする
                if (!isVisited(cell)) {
                    maze.cells[cellIndex].visit();
                    visitedCount++;
                }
            }
        }
    }

    // 未訪問セルをランダムに取得
    private Position randomUnvisitedPosition() {
        while (true) {
            int index = random.nextInt(maze.cells.length);

            Position pos = indexToPosition(index);

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
        for (Direction dir : Direction.values()) {
            Position next = from.cloneMoved(dir);

            if (next.equals(to)) {
                return dir;
            }
        }

        return null;
    }

    // indexからPositionを作成
    private Position indexToPosition(int index) {
        int[] coords = new int[maze.mazeSize.length];

        for (int i = maze.mazeSize.length - 1; i >= 0; i--) {
            coords[i] = index % maze.mazeSize[i];
            index /= maze.mazeSize[i];
        }

        return new Position(coords);
    }
}