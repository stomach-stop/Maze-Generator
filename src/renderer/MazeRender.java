package renderer;

import java.awt.Graphics2D;

/** 指定された領域の中に迷路とプレイヤーを描画する。 */
public interface MazeRender {
    void render(Graphics2D graphics, int width, int height);
}
