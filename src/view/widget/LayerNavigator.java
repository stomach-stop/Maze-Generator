package view.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import view.SliceViewState;

/** 層を重ねた案内図。スクロール位置によらず、図形の外接長方形をクリック範囲にする。 */
public final class LayerNavigator extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int TOP = 90;
    private static final int STEP = 40;
    private final SliceViewState state;

    public LayerNavigator(SliceViewState state, Runnable onSelection) {
        this.state = state;
        setPreferredSize(new Dimension(166, TOP + state.layerCount() * STEP + 20));
        setFocusable(false);
        setToolTipText("層をクリックして閲覧。移動キーで現在層に戻ります。");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (!SwingUtilities.isLeftMouseButton(event)) return;
                for (int layer = 0; layer < state.layerCount(); layer++) {
                    if (layerShape(layer).getBounds().contains(
                            event.getX() / displayScale(), event.getY() / displayScale())) {
                        state.selectLayer(layer);
                        onSelection.run();
                        repaint();
                        return;
                    }
                }
            }
        });
    }

    private double displayScale() {
        return Math.min(1.0, Math.max(1, getWidth()) / 166.0);
    }

    private int layerY(int layer) { return TOP + (state.layerCount() - 1 - layer) * STEP; }

    private Polygon layerShape(int layer) {
        int y = layerY(layer);
        return new Polygon(new int[] { 16, 58, 118, 76 },
                new int[] { y + 16, y, y + 16, y + 32 }, 4);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.scale(displayScale(), displayScale());
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(getFont().deriveFont(12f));
            g.setColor(Color.DARK_GRAY);
            g.drawString("高さの層（Z）", 12, 22);
            g.setColor(new Color(190, 35, 35));
            g.drawString("赤：現在地 Z=" + state.currentLayer(), 12, 43);
            g.setColor(new Color(35, 95, 210));
            g.drawString("青枠：表示 Z=" + state.displayedLayer(), 12, 63);
            for (int layer = 0; layer < state.layerCount(); layer++) {
                Polygon shape = layerShape(layer);
                if (!g.getClipBounds().intersects(shape.getBounds())) continue;
                boolean current = layer == state.currentLayer();
                boolean selected = layer == state.displayedLayer();
                g.setColor(current ? new Color(230, 90, 90) : new Color(221, 228, 237));
                g.fillPolygon(shape);
                g.setStroke(new BasicStroke(selected ? 3f : 1f));
                g.setColor(selected ? new Color(35, 95, 210) : new Color(110, 120, 135));
                g.drawPolygon(shape);
                g.setColor(Color.DARK_GRAY);
                g.drawString("Z=" + layer, 123, layerY(layer) + 21);
            }
        } finally {
            g.dispose();
        }
    }
}
