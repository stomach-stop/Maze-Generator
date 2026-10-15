package renderer;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** 余白と、縦横比を保つ中央配置の計算。 */
public final class ViewLayout {
    private final int horizontalMargin;
    private final int verticalMargin;

    public ViewLayout(int horizontalMargin, int verticalMargin) {
        this.horizontalMargin = horizontalMargin;
        this.verticalMargin = verticalMargin;
    }

    public Rectangle sceneBounds(int width, int height) {
        int x = Math.min(horizontalMargin, Math.max(0, (width - 1) / 2));
        int y = Math.min(verticalMargin, Math.max(0, (height - 1) / 2));
        return new Rectangle(x, y, Math.max(1, width - 2 * x), Math.max(1, height - 2 * y));
    }

    /** 呼び出し側が dispose() する、拡大・移動済みの描画コンテキスト。 */
    public static Graphics2D fit(Graphics2D graphics, int width, int height,
                                 double modelWidth, double modelHeight) {
        double scale = Math.min(Math.max(1, width - 2) / modelWidth,
                Math.max(1, height - 2) / modelHeight);
        Graphics2D result = (Graphics2D) graphics.create();
        result.translate((width - modelWidth * scale) / 2,
                (height - modelHeight * scale) / 2);
        result.scale(scale, scale);
        result.setStroke(new BasicStroke((float) (1 / scale)));
        return result;
    }
}
