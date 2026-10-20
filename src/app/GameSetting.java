package app;

import generation.MazeAlgorithm;

public final class GameSetting {
    private GameSetting() {}

    public static final MazeAlgorithm ALGORITHM = MazeAlgorithm.ALDOUS_BRODER;
    // 空文字なら毎回ランダム。リザルトの6文字を指定すると同じ条件の迷路を再現する。
    public static final String SEED = "";

    public static final int[] MAZE_SIZE = {5, 5, 5};
    public static final int[] START_POSITION = {0, 0, 0};

}
