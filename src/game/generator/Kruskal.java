package game.generator;

import java.util.*;
import game.*;

public class Kruskal implements Generator {
    private final Maze maze;
    private final Random random;

    public Kruskal(Maze maze) {
        this(maze, new Random());
    }

    public Kruskal(Maze maze, Random random) {
        this.maze = java.util.Objects.requireNonNull(maze);
        this.random = java.util.Objects.requireNonNull(random);
    }

    @Override
    public void generate(Position pos) {
        maze.beginGeneration(pos);
        // すべての壁を「候補」として取得
        List<Wall> walls = new ArrayList<>();

        int[] coordinates = new int[maze.dimensions()];
        collectWalls(walls, coordinates, 0);

        // 壁をランダムな順番にする
        Collections.shuffle(walls, random);

        // 各セルがどの集合に属しているかを管理
        int cellCount = maze.cellCount();
        UnionFind uf = new UnionFind(cellCount);

        // 壁を1つずつ確認
        for (Wall wall : walls) {
            int current = maze.toIndex(wall.pos);
            Position nextPos = wall.pos.cloneMoved(wall.dir);
            int next = maze.toIndex(nextPos);

            // 2つのセルが別々の集合なら壁を壊す
            if (uf.find(current) != uf.find(next)) {
                maze.openPassage(wall.pos, wall.dir);

                uf.union(current, next);
            }
        }
    }

    // 迷路内の壁を取得
    private void collectWalls(List<Wall> walls, int[] coords, int dimension) {
        if (dimension == maze.dimensions()) {
            Position pos = new Position(coords.clone());

            for (Direction dir : maze.getDirections()) {
                Position next = pos.cloneMoved(dir);

                // 迷路外の壁は除外
                if (!maze.isInBounds(next)) {
                    continue;
                }

                // 同じ壁を2回登録しない
                int current = maze.toIndex(pos);
                int neighbor = maze.toIndex(next);

                if (current < neighbor) {
                    walls.add(new Wall(pos, dir));
                }
            }

            return;
        }

        for (int i = 0; i < maze.size(dimension); i++) {
            coords[dimension] = i;
            collectWalls(walls, coords, dimension + 1);
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

    // Union-Find（素集合データ構造）
    private static class UnionFind {
        private int[] parent;

        UnionFind(int size) {
            parent = new int[size];

            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }

            return parent[x];
        }

        void union(int x, int y) {
            x = find(x);
            y = find(y);

            if (x != y) {
                parent[y] = x;
            }
        }
    }
}
