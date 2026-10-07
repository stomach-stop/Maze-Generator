package game;

public class Position { //座標を管理
    public int[] coords; //座標の値を格納する配列
    public Position(int... values) {
        this.coords = values.clone();
    }

    public Position cloneMoved(Direction dir) {
        int[] clone = this.coords.clone();
        for (int i = 0; i < coords.length; i++) {
            clone[i] += dir.get()[i];
        }
        return new Position(clone);
    }

    //以下AI生成
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position)) {
            return false;
        }
        
        Position other = (Position) obj; //型キャスト
        return java.util.Arrays.equals(coords, other.coords);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(coords);
    }
}