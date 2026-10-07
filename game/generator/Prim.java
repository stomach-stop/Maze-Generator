package game.generator;

import java.util.*;
import game.*;

public class Prim implements Generator {
    private Maze maze;

    public Prim(Maze maze) {
        this.maze = maze;
        System.out.println("Prim");
    }

    @Override
    public void generate(Position pos) {
        // 現在地を訪問済みにする
        maze.cells[maze.toIndex(pos)].visit();

        // 壁の候補を入れるリスト
        List<Wall> walls = new ArrayList<>();

        // 現在地から伸ばせる壁を追加
        addWalls(pos, walls);

        while (!walls.isEmpty()) {
            // ランダムに壁を1つ選ぶ
            int index = new Random().nextInt(walls.size());
            Wall wall = walls.remove(index);

            Position next = wall.pos.cloneMoved(wall.dir);

            // 移動先が未訪問なら壁を壊す
            if (maze.isInBounds(next)
                    && maze.canVisit(next)) {

                int currentIndex = maze.toIndex(wall.pos);
                int nextIndex = maze.toIndex(next);

                // 壁を削除
                maze.cells[currentIndex].removeWall(wall.dir);
                maze.cells[nextIndex].removeWall(wall.dir.opposite());

                // 次のセルを訪問済みにする
                maze.cells[nextIndex].visit();

                // 新しく追加されたセルから伸ばせる壁を追加
                addWalls(next, walls);
            }
        }
    }

    // 指定したセルから伸ばせる壁を追加
    private void addWalls(Position pos, List<Wall> walls) {
        for (Direction dir : Direction.values()) {
            Position next = pos.cloneMoved(dir);

            if (maze.isInBounds(next)
                    && maze.canVisit(next)) {

                walls.add(new Wall(pos, dir));
            }
        }
    }

    // 壁を表すクラス
    private static class Wall {
        Position pos;
        Direction dir;

        Wall(Position pos, Direction dir) {
            this.pos = pos;
            this.dir = dir;
        }
    }
}