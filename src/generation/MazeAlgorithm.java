package generation;

import java.util.Random;
import java.util.function.BiFunction;
import model.Maze;

/** すべての生成方式に、同じ入口からシード付き乱数を渡す。 */
public enum MazeAlgorithm {
    ALDOUS_BRODER("Aldous-Broder", AldousBroder::new),
    WILSON("Wilson", Wilson::new),
    PRIM("Prim", Prim::new),
    KRUSKAL("Kruskal", Kruskal::new),
    RECURSIVE_BACKTRACKER("Recursive Backtracker", RecursiveBacktracker::new);

    private final String displayName;
    private final BiFunction<Maze, Random, Generator> factory;

    MazeAlgorithm(String displayName, BiFunction<Maze, Random, Generator> factory) {
        this.displayName = displayName;
        this.factory = factory;
    }

    public String displayName() { return displayName; }
    public Generator create(Maze maze, long seed) { return factory.apply(maze, new Random(seed)); }
}
