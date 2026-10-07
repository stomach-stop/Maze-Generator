package game;

public class GameSetting {
    private GameSetting() {}

    //描画は30まで。以降は生成時間計測
    public static final int[] MAZE_SIZE = {8, 8, 4};

    public static final int[] START_POSITION = {0, 0, 0};

    public static final int CELL_SIZE = 30;

    public static final int WINDOW_WIDTH = 300;
    public static final int WINDOW_HEIGHT = 300;
    //30, 30のとき917, 940
}