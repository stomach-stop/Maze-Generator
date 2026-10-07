package game;
import java.util.Arrays;

public class Cell {
    public boolean visited; //訪問済みか
    public boolean[] walls; //各方向の壁の有無

    public Cell(int dimensions) {
        this.visited = false;
        this.walls = new boolean[dimensions * 2];
        Arrays.fill(this.walls, true);
    }
    
    public void visit() { this.visited = true; }

    public boolean hasWall(Direction dir) {
        return walls[dir.ordinal()];
    }

    public void removeWall(Direction dir) {
        walls[dir.ordinal()] = false;
    }
}