package game;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import renderer.*;

public class GamePanel extends JPanel {

    private Maze maze;
    private Player player;

    private MazeRender render;

    /*
     * 回転ボタン
     */
    private Rectangle upButton;
    private Rectangle downButton;
    private Rectangle leftButton;
    private Rectangle rightButton;

    private static final int BUTTON_SIZE = 35;
    private static final int BUTTON_GAP = 3;

    public GamePanel(
            Maze maze,
            Player player) {

        this.maze = maze;
        this.player = player;

        this.render =
            new SkeletonRender(
                maze,
                player
            );

        /*
         * =============================
         * マウスクリック処理
         * =============================
         */

        addMouseListener(
            new MouseAdapter() {

                @Override
                public void mouseClicked(
                        MouseEvent e) {

                    handleMouseClick(
                        e.getX(),
                        e.getY()
                    );
                }
            }
        );
    }

    @Override
    protected void paintComponent(
            Graphics g) {

        super.paintComponent(g);

        render.drawMaze(g);
        render.drawPlayer(g);

        /*
         * Skeletonのときだけ
         * 回転ボタンを表示
         */
        if (
            render
                instanceof SkeletonRender
        ) {

            drawRotationButtons(g);
        }
    }

    /*
     * =============================
     * 表示切替
     * =============================
     */

    public void setXY() {

        this.render =
            new XYRender(
                maze,
                player
            );

        repaint();
    }

    public void setXZ() {

        this.render =
            new XZRender(
                maze,
                player
            );

        repaint();
    }

    public void setYZ() {

        this.render =
            new YZRender(
                maze,
                player
            );

        repaint();
    }

    public void setSkeleton() {

        this.render =
            new SkeletonRender(
                maze,
                player
            );

        repaint();
    }

    /*
     * =============================
     * 回転
     * =============================
     */

    public void rotateLeft() {

        if (
            render
                instanceof SkeletonRender
        ) {

            SkeletonRender skeleton =
                (SkeletonRender) render;

            skeleton.rotateLeft();

            repaint();
        }
    }

    public void rotateRight() {

        if (
            render
                instanceof SkeletonRender
        ) {

            SkeletonRender skeleton =
                (SkeletonRender) render;

            skeleton.rotateRight();

            repaint();
        }
    }

    public void rotateUp() {

        if (
            render
                instanceof SkeletonRender
        ) {

            SkeletonRender skeleton =
                (SkeletonRender) render;

            skeleton.rotateUp();

            repaint();
        }
    }

    public void rotateDown() {

        if (
            render
                instanceof SkeletonRender
        ) {

            SkeletonRender skeleton =
                (SkeletonRender) render;

            skeleton.rotateDown();

            repaint();
        }
    }

    /*
     * =============================
     * 回転ボタン描画
     * =============================
     */

    private void drawRotationButtons(
            Graphics g) {

        Graphics2D g2 =
            (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        /*
         * 画面右下に配置
         */
        int centerX =
            getWidth()
                - BUTTON_SIZE * 2;

        int centerY =
            getHeight()
                - BUTTON_SIZE * 2;

        /*
         * ↑
         */
        upButton =
            new Rectangle(
                centerX,
                centerY - BUTTON_SIZE - BUTTON_GAP,
                BUTTON_SIZE,
                BUTTON_SIZE
            );

        /*
         * ↓
         */
        downButton =
            new Rectangle(
                centerX,
                centerY + BUTTON_SIZE + BUTTON_GAP,
                BUTTON_SIZE,
                BUTTON_SIZE
            );

        /*
         * ←
         */
        leftButton =
            new Rectangle(
                centerX - BUTTON_SIZE - BUTTON_GAP,
                centerY,
                BUTTON_SIZE,
                BUTTON_SIZE
            );

        /*
         * →
         */
        rightButton =
            new Rectangle(
                centerX + BUTTON_SIZE + BUTTON_GAP,
                centerY,
                BUTTON_SIZE,
                BUTTON_SIZE
            );

        /*
         * ボタン背景
         */
        drawButton(
            g2,
            upButton,
            "↑"
        );

        drawButton(
            g2,
            downButton,
            "↓"
        );

        drawButton(
            g2,
            leftButton,
            "←"
        );

        drawButton(
            g2,
            rightButton,
            "→"
        );
    }

    /*
     * ボタン1個を描画
     */
    private void drawButton(
            Graphics2D g2,
            Rectangle rect,
            String text) {

        /*
         * 背景
         */
        g2.setColor(
            new Color(
                235,
                235,
                235
            )
        );

        g2.fillRoundRect(
            rect.x,
            rect.y,
            rect.width,
            rect.height,
            8,
            8
        );

        /*
         * 枠線
         */
        g2.setColor(Color.DARK_GRAY);

        g2.setStroke(
            new BasicStroke(1.5f)
        );

        g2.drawRoundRect(
            rect.x,
            rect.y,
            rect.width,
            rect.height,
            8,
            8
        );

        /*
         * 矢印
         */
        g2.setFont(
            new Font(
                Font.SANS_SERIF,
                Font.BOLD,
                20
            )
        );

        FontMetrics fm =
            g2.getFontMetrics();

        int textWidth =
            fm.stringWidth(text);

        int textHeight =
            fm.getAscent();

        int textX =
            rect.x
                + (
                    rect.width
                    - textWidth
                ) / 2;

        int textY =
            rect.y
                + (
                    rect.height
                    + textHeight
                ) / 2
                - 3;

        g2.setColor(Color.BLACK);

        g2.drawString(
            text,
            textX,
            textY
        );
    }

    /*
     * =============================
     * マウスクリック判定
     * =============================
     */

    private void handleMouseClick(
            int mouseX,
            int mouseY) {

        /*
         * Skeleton表示以外では
         * 何もしない
         */
        if (
            !(render
                instanceof SkeletonRender)
        ) {

            return;
        }

        /*
         * まだ描画されていない場合
         */
        if (
            upButton == null ||
            downButton == null ||
            leftButton == null ||
            rightButton == null
        ) {

            return;
        }

        if (
            upButton.contains(
                mouseX,
                mouseY
            )
        ) {

            rotateUp();

        } else if (
            downButton.contains(
                mouseX,
                mouseY
            )
        ) {

            rotateDown();

        } else if (
            leftButton.contains(
                mouseX,
                mouseY
            )
        ) {

            rotateLeft();

        } else if (
            rightButton.contains(
                mouseX,
                mouseY
            )
        ) {

            rotateRight();
        }
    }
}

/*
package game;
import javax.swing.JPanel;
import java.awt.Graphics;
import renderer.*;

public class GamePanel extends JPanel {
    private Maze maze;
    private Player player;
    private MazeRender render;

    public GamePanel(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
        this.render = new SkeletonRender(maze, player);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render.drawMaze(g);
        render.drawPlayer(g);
    }

    public void setXY() {
        this.render = new XYRender(maze, player);
    }

    public void setXZ() {
        this.render = new XZRender(maze, player);
    }

    public void setYZ() {
        this.render = new YZRender(maze, player);
    }

    public void setSkeleton() {
        this.render = new SkeletonRender(maze, player);
    }
}
*/