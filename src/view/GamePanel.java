package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import model.Maze;
import model.Player;
import view.camera.OrbitCamera;
import view.renderer.MazeRender;
import view.renderer.SkeletonRender;
import view.renderer.XYRender;
import view.renderer.XZRender;
import view.renderer.YZRender;
import view.widget.AxesRender;
import view.widget.HudRender;
import view.widget.LayerNavigator;

/** ウィンドウごとの表示方式と描画の組み立てを管理する。 */
public final class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Player player;
    private final JPanel actionSidebar = new JPanel(new BorderLayout());
    private final JButton trailButton = new JButton();
    private final SliceViewState sliceState;
    private final JScrollPane layerSidebar;
    private final Map<ViewMode, MazeRender> renders = new EnumMap<>(ViewMode.class);
    private final OrbitCamera camera;
    private final ViewLayout layout = new ViewLayout(ViewSettings.VIEW_MARGIN, ViewSettings.HUD_MARGIN);
    private final HudRender hud = new HudRender();
    private final AxesRender axes = new AxesRender();
    private ViewMode viewMode = ViewMode.XY;

    public GamePanel(Maze maze, Player player, Runnable resetPosition, Runnable toggleTrail) {
        this.player = player;
        sliceState = new SliceViewState(player, maze.size(2));
        setLayout(new BorderLayout());
        actionSidebar.setOpaque(false);
        actionSidebar.setBorder(BorderFactory.createEmptyBorder(ViewSettings.HUD_MARGIN, 8, 12, 8));
        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 12));
        buttons.setOpaque(false);
        JButton resetButton = new JButton("位置初期化");
        resetButton.setToolTipText("開始位置に戻ります。時間・移動回数・通過履歴は保持します。");
        resetButton.setFocusable(false);
        trailButton.setFocusable(false);
        resetButton.addActionListener(event -> {
            resetPosition.run();
            requestFocusInWindow();
        });
        trailButton.addActionListener(event -> {
            toggleTrail.run();
            requestFocusInWindow();
        });
        buttons.add(resetButton);
        buttons.add(trailButton);
        actionSidebar.add(buttons, BorderLayout.NORTH);
        add(actionSidebar, BorderLayout.WEST);
        refreshControls();
        LayerNavigator navigator = new LayerNavigator(sliceState, () -> {
            requestFocusInWindow();
            repaint();
        });
        navigator.setFont(getFont());
        layerSidebar = new JScrollPane(navigator,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layerSidebar.setPreferredSize(new Dimension(190, 0));
        layerSidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 4));
        layerSidebar.setFocusable(false);
        layerSidebar.getVerticalScrollBar().setUnitIncrement(40);
        add(layerSidebar, BorderLayout.EAST);
        camera = new OrbitCamera(SkeletonRender.boundingRadius(maze), ViewSettings.SKELETON_CELL_SPACING);
        renders.put(ViewMode.XY, new XYRender(maze, player, sliceState::displayedLayer));
        renders.put(ViewMode.XZ, new XZRender(maze, player));
        renders.put(ViewMode.YZ, new YZRender(maze, player));
        renders.put(ViewMode.SKELETON, new SkeletonRender(maze, player, camera));
        setPreferredSize(ViewSettings.preferredViewSize());
        setFocusable(true);
    }

    public void refreshControls() {
        trailButton.setText("赤線：" + (player.isTrailVisible() ? "表示中" : "非表示"));
        trailButton.setToolTipText("クリックで通過経路の赤線を表示／非表示に切り替えます。");
    }

    public void setView(ViewMode mode) {
        if (mode == null) throw new IllegalArgumentException("View mode is required.");
        viewMode = mode;
        sliceState.followPlayer();
        layerSidebar.setVisible(mode == ViewMode.XY);
        revalidate();
        repaint();
    }

    public void followPlayerLayer() {
        sliceState.followPlayer();
        repaint();
    }

    public Optional<OrbitCamera> activeCamera() {
        return viewMode == ViewMode.SKELETON ? Optional.of(camera) : Optional.empty();
    }

    @Override
    public void doLayout() {
        // 狭いウィンドウでも迷路の描画領域を確保する。
        int sidebarWidth = Math.min(190, Math.max(1, getWidth() / 4));
        layerSidebar.setPreferredSize(new Dimension(sidebarWidth, 0));
        actionSidebar.setPreferredSize(new Dimension(sidebarWidth, 0));
        super.doLayout();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle bounds = layout.sceneBounds(getWidth(), getHeight());
        {
            // 左右の操作欄を避け、迷路はウィンドウ中央に保つ。
            int sideWidth = Math.max(actionSidebar.getWidth(),
                    layerSidebar.isVisible() ? layerSidebar.getWidth() : 0);
            int margin = Math.max(bounds.x, sideWidth + 12);
            margin = Math.min(margin, Math.max(0, (getWidth() - 1) / 2));
            bounds.x = margin;
            bounds.width = Math.max(1, getWidth() - margin * 2);
        }
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
