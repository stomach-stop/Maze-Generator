package game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.JPanel;
import renderer.AxesRender;
import renderer.HudRender;
import renderer.MazeRender;
import renderer.OrbitCamera;
import renderer.SkeletonRender;
import renderer.ViewLayout;
import renderer.XYRender;
import renderer.XZRender;
import renderer.YZRender;

/** ウィンドウごとの表示方式と描画の組み立てを管理する。 */
public final class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public enum ViewMode { XY, XZ, YZ, SKELETON }

    private final Player player;
    private final Map<ViewMode, MazeRender> renders = new EnumMap<>(ViewMode.class);
    private final OrbitCamera camera;
    private final ViewLayout layout = new ViewLayout(GameSetting.VIEW_MARGIN, GameSetting.HUD_MARGIN);
    private final HudRender hud = new HudRender();
    private final AxesRender axes = new AxesRender();
    private ViewMode viewMode = ViewMode.XY;

    public GamePanel(Maze maze, Player player) {
        this.player = player;
        camera = new OrbitCamera(SkeletonRender.boundingRadius(maze), GameSetting.SKELETON_CELL_SPACING);
        renders.put(ViewMode.XY, new XYRender(maze, player));
        renders.put(ViewMode.XZ, new XZRender(maze, player));
        renders.put(ViewMode.YZ, new YZRender(maze, player));
        renders.put(ViewMode.SKELETON, new SkeletonRender(maze, player, camera));
        setPreferredSize(GameSetting.preferredViewSize());
        setFocusable(true);
    }

    public void setView(ViewMode mode) {
        if (mode == null) throw new IllegalArgumentException("View mode is required.");
        viewMode = mode;
        repaint();
    }

    public Optional<OrbitCamera> activeCamera() {
        return viewMode == ViewMode.SKELETON ? Optional.of(camera) : Optional.empty();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle bounds = layout.sceneBounds(getWidth(), getHeight());
        Graphics2D scene = (Graphics2D) graphics.create(bounds.x, bounds.y, bounds.width, bounds.height);
        try {
            scene.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renders.get(viewMode).render(scene, bounds.width, bounds.height);
        } finally {
            scene.dispose();
        }
        Graphics2D overlay = (Graphics2D) graphics.create();
        try {
            overlay.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            hud.render(overlay, player.position());
            activeCamera().ifPresent(active -> axes.render(overlay, active, getWidth() - 64, 54, 30));
        } finally {
            overlay.dispose();
        }
    }
}
