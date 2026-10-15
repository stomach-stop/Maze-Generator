package game;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import renderer.OrbitCamera;

/** 入力を操作に変換する。ウィンドウ作成や描画処理の実装には依存しない。 */
public final class InputHandler extends MouseAdapter implements KeyListener {
    private final Player player;
    private final Consumer<GamePanel.ViewMode> changeView;
    private final Supplier<Optional<OrbitCamera>> activeCamera;
    private final Runnable openWindow;
    private final Runnable repaintAll;
    private Point lastDragPoint;

    public InputHandler(Player player, Consumer<GamePanel.ViewMode> changeView,
                        Supplier<Optional<OrbitCamera>> activeCamera,
                        Runnable openWindow, Runnable repaintAll) {
        this.player = player;
        this.changeView = changeView;
        this.activeCamera = activeCamera;
        this.openWindow = openWindow;
        this.repaintAll = repaintAll;
    }

    public void install(JComponent component) {
        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_D: player.move(Direction.PositiveX); break;
            case KeyEvent.VK_A: player.move(Direction.NegativeX); break;
            case KeyEvent.VK_S: player.move(Direction.PositiveY); break;
            case KeyEvent.VK_W: player.move(Direction.NegativeY); break;
            case KeyEvent.VK_E: player.move(Direction.PositiveZ); break;
            case KeyEvent.VK_Q: player.move(Direction.NegativeZ); break;
            case KeyEvent.VK_0: openWindow.run(); break;
            case KeyEvent.VK_1: select(GamePanel.ViewMode.XY); break;
            case KeyEvent.VK_2: select(GamePanel.ViewMode.XZ); break;
            case KeyEvent.VK_3: select(GamePanel.ViewMode.YZ); break;
            case KeyEvent.VK_4: select(GamePanel.ViewMode.SKELETON); break;
            case KeyEvent.VK_9: player.resetPosition(); break;
            default: return;
        }
        repaintAll.run();
    }

    private void select(GamePanel.ViewMode mode) {
        lastDragPoint = null;
        changeView.accept(mode);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        event.getComponent().requestFocusInWindow();
        if (SwingUtilities.isLeftMouseButton(event) && activeCamera.get().isPresent()) {
            lastDragPoint = event.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) { lastDragPoint = null; }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (lastDragPoint == null) return;
        activeCamera.get().ifPresent(camera -> {
            camera.rotateByPixels(event.getX() - lastDragPoint.x, event.getY() - lastDragPoint.y);
            lastDragPoint = event.getPoint();
            event.getComponent().repaint();
        });
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        activeCamera.get().ifPresent(camera -> {
            camera.zoom(event.getPreciseWheelRotation());
            event.getComponent().repaint();
        });
    }

    @Override public void keyReleased(KeyEvent event) {}
    @Override public void keyTyped(KeyEvent event) {}
}
