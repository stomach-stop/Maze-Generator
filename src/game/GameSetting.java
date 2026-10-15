package game;

import java.awt.Dimension;

public class GameSetting {
    private GameSetting() {}

    public static final int[] MAZE_SIZE = {5, 5, 5};
    public static final int[] START_POSITION = {0, 0, 0};

    // Model units. Actual cell size is fitted to the available drawing area.
    public static final int CELL_SIZE = 30;
    public static final double SKELETON_CELL_SPACING = CELL_SIZE * 1.5;
    public static final double SKELETON_CELL_RADIUS = CELL_SIZE * 0.10;
    public static final double SKELETON_PLAYER_RADIUS = CELL_SIZE * 0.15;
    public static final double SKELETON_PASSAGE_WIDTH = CELL_SIZE * 0.05;

    // Initial content dimensions; the user can resize each window independently.
    public static final int INITIAL_VIEW_WIDTH = 800;
    public static final int INITIAL_VIEW_HEIGHT = 800;
    public static final int VIEW_MARGIN = 32;
    public static final int HUD_MARGIN = 104;

    public static Dimension preferredViewSize() {
        return new Dimension(INITIAL_VIEW_WIDTH, INITIAL_VIEW_HEIGHT);
    }
}