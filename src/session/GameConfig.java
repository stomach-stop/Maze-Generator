package session;

import generation.GameSeed;
import generation.MazeAlgorithm;
import java.util.Locale;
import java.util.Objects;
import model.Position;

/** 1回の生成条件。空のシードは開始時に具体的な値へ確定する。 */
public final class GameConfig {
    public static final int MAX_AXIS_SIZE = 30;
    private final MazeAlgorithm algorithm;
    private final String seed;
    private final int[] sizes;
    private final Position start;

    public GameConfig(MazeAlgorithm algorithm, String seed, int[] sizes, Position start) {
        this.algorithm = Objects.requireNonNull(algorithm);
        this.seed = seed == null ? "" : seed.trim().toUpperCase(Locale.ROOT);
        if (!this.seed.isEmpty() && !this.seed.matches("[0-9A-Z]{1,6}"))
            throw new IllegalArgumentException("シードは英数字1〜6文字、または空欄にしてください。");
        this.sizes = sizes.clone();
        this.start = Objects.requireNonNull(start);
        if (sizes.length != 3 || start.dimensions() != 3)
            throw new IllegalArgumentException("サイズと開始位置は3次元で指定してください。");
        for (int axis = 0; axis < 3; axis++) {
            if (sizes[axis] < 1 || sizes[axis] > MAX_AXIS_SIZE)
                throw new IllegalArgumentException("各サイズは1〜" + MAX_AXIS_SIZE + "にしてください。");
            if (start.coordinate(axis) < 0 || start.coordinate(axis) >= sizes[axis])
                throw new IllegalArgumentException("開始位置が迷路の範囲外です。");
        }
    }

    public MazeAlgorithm algorithm() { return algorithm; }
    public String seed() { return seed; }
    public int[] sizes() { return sizes.clone(); }
    public Position start() { return start; }
    public GameConfig withSeed(String value) { return new GameConfig(algorithm, value, sizes, start); }
    public GameConfig resolveSeed() { return withSeed(GameSeed.fromSetting(seed).code()); }
}
