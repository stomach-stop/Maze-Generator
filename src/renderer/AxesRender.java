package renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.Comparator;
import renderer.OrbitCamera.ViewPoint;

/** カメラと同じ回転を使って、画面上の座標軸だけを描画する。 */
public final class AxesRender {
    /** Fixed-size screen indicator: red X, green Y, blue Z, pointing positive. */
    public void render(Graphics2D graphics, OrbitCamera camera, int centerX, int centerY, int length) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ViewPoint[] axes = { camera.rotateVector(1, 0, 0), camera.rotateVector(0, 1, 0), camera.rotateVector(0, 0, 1) };
            Color[] colors = { new Color(195, 45, 45), new Color(30, 135, 70), new Color(45, 95, 205) };
            String[] labels = { "+X", "+Y", "+Z" };
            Integer[] order = { 0, 1, 2 };
            java.util.Arrays.sort(order, Comparator.comparingDouble((Integer i) -> axes[i].depth).reversed());
            g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            for (int i : order) {
                double dx = axes[i].x * length;
                double dy = -axes[i].y * length;
                double endX = centerX + dx;
                double endY = centerY + dy;
                g.setColor(colors[i]);
                // Faint dashed negative half; the labelled arrow is the positive half.
                g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        10f, new float[] { 3f, 3f }, 0f));
                g.draw(new Line2D.Double(centerX - dx * 0.6, centerY - dy * 0.6, centerX, centerY));
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Double(centerX, centerY, endX, endY));
                double projectedLength = Math.hypot(dx, dy);
                if (projectedLength > 5) {
                    double ux = dx / projectedLength;
                    double uy = dy / projectedLength;
                    g.draw(new Line2D.Double(endX, endY, endX - 6 * ux + 3 * uy, endY - 6 * uy - 3 * ux));
                    g.draw(new Line2D.Double(endX, endY, endX - 6 * ux - 3 * uy, endY - 6 * uy + 3 * ux));
                } else {
                    // End-on axis: dot means towards the viewer; cross means away.
                    g.drawOval((int) endX - 3, (int) endY - 3, 6, 6);
                    if (axes[i].depth < 0) {
                        g.fillOval((int) endX - 1, (int) endY - 1, 3, 3);
                    } else {
                        g.drawLine((int) endX - 2, (int) endY - 2, (int) endX + 2, (int) endY + 2);
                        g.drawLine((int) endX - 2, (int) endY + 2, (int) endX + 2, (int) endY - 2);
                    }
                }
                int labelX = (int) Math.round(endX) + (dx < -5 ? -21 : 5);
                int labelY = (int) Math.round(endY) + (dy > 5 ? 14 : -5);
                g.drawString(labels[i], labelX, labelY);
            }
        } finally {
            g.dispose();
        }
    }

}
