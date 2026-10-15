package renderer;

import game.Position;
import java.awt.Color;
import java.awt.Graphics2D;

/** プレイヤー座標など、迷路の拡大率に左右されない補助表示。 */
public final class HudRender {
    public void render(Graphics2D graphics, Position position) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setColor(new Color(35, 40, 48));
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString(String.format("X: %d   Y: %d   Z: %d", position.coordinate(0),
                    position.dimensions() > 1 ? position.coordinate(1) : 0,
                    position.dimensions() > 2 ? position.coordinate(2) : 0), 16, 28);
        } finally {
            g.dispose();
        }
    }
}
