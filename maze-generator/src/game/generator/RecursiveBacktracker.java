package game.generator;

import java.util.*;
import game.*;

public class RecursiveBacktracker implements Generator {
    private Maze maze;

    public RecursiveBacktracker(Maze maze) {
        this.maze = maze;
        System.out.println("RecursiveBacktracker");
    }

    @Override
    public void generate(Position pos) {
        maze.cells[maze.toIndex(pos)].visit(); //現在地を訪問済みにする

        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < maze.mazeSize.length * 2; i++) {
            order.add(i);
        }

        Collections.shuffle(order); //ランダムに方向を決める
        
        for (int i : order) {
            Direction dir = Direction.values()[i]; //方向を取得
            Position next = pos.cloneMoved(dir); //次の位置を計算
            
            if (maze.isInBounds(next) && maze.canVisit(next)) {
                maze.cells[maze.toIndex(pos)].removeWall(dir); //現在方向の壁を削除
                maze.cells[maze.toIndex(next)].removeWall(dir.opposite()); //次の位置の反対方向の壁を削除
                generate(next);
            }
        }
    }
}