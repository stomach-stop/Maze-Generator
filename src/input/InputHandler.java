package input;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import model.Direction;
import model.Player;
import view.ViewMode;
import view.camera.OrbitCamera;

/** 入力を操作に変換する。ウィンドウ作成や描画処理の実装には依存しない。 */
public final class InputHandler extends MouseAdapter implements KeyListener {
    private final Player player;
    private final Consumer<ViewMode> changeView;
    private final Supplier<Optional<OrbitCamera>> activeCamera;
    private final Runnable openWindow;
    private final Runnable afterInput;
    private final Runnable followPlayerLayers;
    private Point lastDragPoint;
    private final BooleanSupplier inputEnabled;

    public InputHandler(Player player, Consumer<ViewMode> changeView,
                        Supplier<Optional<OrbitCamera>> activeCamera,
                        Runnable openWindow, Runnable afterInput, Runnable followPlayerLayers,
                        BooleanSupplier inputEnabled) {
        this.player = player;
        this.inputEnabled = inputEnabled;
        this.changeView = changeView;
        this.activeCamera = activeCamera;
        this.openWindow = openWindow;
        this.afterInput = afterInput;
        this.followPlayerLayers = followPlayerLayers;
    }

    public void install(JComponent component) {
        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (!inputEnabled.getAsBoolean()) return;
        switch (event.getKeyCode()) {
            case KeyEvent.VK_D: move(Direction.PositiveX); break;
            case KeyEvent.VK_A: move(Direction.NegativeX); break;
            case KeyEvent.VK_S: move(Direction.PositiveY); break;
            case KeyEvent.VK_W: move(Direction.NegativeY); break;
            case KeyEvent.VK_E: move(Direction.PositiveZ); break;
            case KeyEvent.VK_Q: move(Direction.NegativeZ); break;
            case KeyEvent.VK_0: openWindow.run(); break;
            case KeyEvent.VK_1: select(ViewMode.XY); break;
            case KeyEvent.VK_2: select(ViewMode.XZ); break;
            case KeyEvent.VK_3: select(ViewMode.YZ); break;
            case KeyEvent.VK_4: select(ViewMode.SKELETON); break;
            default: return;
        }
        afterInput.run();
    }

    private void move(Direction direction) {
        // 壁で移動できない入力でも閲覧モードを終了する。
        followPlayerLayers.run();
        player.move(direction);
    }

    private void select(ViewMode mode) {
        lastDragPoint = null;
        changeView.accept(mode);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (!inputEnabled.getAsBoolean()) return;
        event.getComponent().requestFocusInWindow();
        if (SwingUtilities.isLeftMouseButton(event) && activeCamera.get().isPresent()) {
            lastDragPoint = event.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) { lastDragPoint = null; }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (!inputEnabled.getAsBoolean()) return;
        if (lastDragPoint == null) return;
        activeCamera.get().ifPresent(camera -> {
            camera.rotateByPixels(event.getX() - lastDragPoint.x, event.getY() - lastDragPoint.y);
            lastDragPoint = event.getPoint();
            event.getComponent().repaint();
        });
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        if (!inputEnabled.getAsBoolean()) return;
        activeCamera.get().ifPresent(camera -> {
            camera.zoom(event.getPreciseWheelRotation());
            event.getComponent().repaint();
        });
    }

    @Override public void keyReleased(KeyEvent event) {}
    @Override public void keyTyped(KeyEvent event) {}
}
