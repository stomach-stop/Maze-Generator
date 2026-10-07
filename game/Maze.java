package game;
public class Maze { //セルの集合を管理
    public Cell[] cells; //セルの集合
    public int[] mazeSize; //各軸の大きさ

    public Maze(int... values) { //迷路の大きさを決定
        this.cells = new Cell[product(values)];
        this.mazeSize = values;
        generateMaze();
    }

    private int product(int[] values) { //配列の要素の積を計算
        int result = 1;
        for (int value : values) {
            result *= value;
        }
        return result;
    }

    private void generateMaze() { //迷路を生成
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(mazeSize.length);
        }
    }

    public boolean isInBounds(Position pos) {
        for (int i = 0; i < mazeSize.length; i++) {
            if (pos.coords[i] < 0 || pos.coords[i] >= mazeSize[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean canVisit(Position pos) {
        return cells[toIndex(pos)].visited == false;
    }

    public int toIndex(Position pos) {
        int index = 0;
        int stride = 1; //各次元の重み

        for (int i = 0; i < mazeSize.length; i++) {
            index += pos.coords[i] * stride;
            stride *= mazeSize[i];
        }
        return index;
    }
}
