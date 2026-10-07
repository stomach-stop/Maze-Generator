package renderer;

import game.Cell;
import game.Direction;
import game.GameSetting;
import game.Maze;
import game.Player;
import game.Position;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class SkeletonRender implements MazeRender {

    private Maze maze;
    private Player player;

    private static final int MARGIN = 25;

    private static final int CELL_POINT_SIZE = 6;
    private static final int PLAYER_POINT_SIZE = 14;

    // 1回の回転角度
    private static final double ROTATE_STEP = 45.0;

    // 左右方向
    private double yaw = 45.0;

    // 上下方向
    private double pitch = 30.0;

    private double scale;
    private double offsetX;
    private double offsetY;

    public SkeletonRender(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
    }

    @Override
    public void drawMaze(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        updateProjection(g2);

        int sizeX = maze.mazeSize[0];

        int sizeY =
            maze.mazeSize.length >= 2
                ? maze.mazeSize[1]
                : 1;

        int sizeZ =
            maze.mazeSize.length >= 3
                ? maze.mazeSize[2]
                : 1;

        /*
         * =========================
         * 通路
         * =========================
         */

        g2.setColor(Color.GRAY);

        for (int z = 0; z < sizeZ; z++) {

            for (int y = 0; y < sizeY; y++) {

                for (int x = 0; x < sizeX; x++) {

                    Position pos =
                        new Position(x, y, z);

                    Cell cell =
                        maze.cells[
                            maze.toIndex(pos)
                        ];

                    Point p =
                        project(x, y, z);

                    /*
                     * +X
                     */
                    if (
                        x + 1 < sizeX &&
                        !cell.hasWall(
                            Direction.PositiveX
                        )
                    ) {

                        Point next =
                            project(
                                x + 1,
                                y,
                                z
                            );

                        g2.setStroke(
                            new BasicStroke(2.0f)
                        );

                        g2.drawLine(
                            p.x,
                            p.y,
                            next.x,
                            next.y
                        );
                    }

                    /*
                     * +Y
                     */
                    if (
                        maze.mazeSize.length >= 2 &&
                        y + 1 < sizeY &&
                        !cell.hasWall(
                            Direction.PositiveY
                        )
                    ) {

                        Point next =
                            project(
                                x,
                                y + 1,
                                z
                            );

                        g2.setStroke(
                            new BasicStroke(2.0f)
                        );

                        g2.drawLine(
                            p.x,
                            p.y,
                            next.x,
                            next.y
                        );
                    }

                    /*
                     * +Z
                     */
                    if (
                        maze.mazeSize.length >= 3 &&
                        z + 1 < sizeZ &&
                        !cell.hasWall(
                            Direction.PositiveZ
                        )
                    ) {

                        Point next =
                            project(
                                x,
                                y,
                                z + 1
                            );

                        // Z方向だけ少し太くする
                        g2.setStroke(
                            new BasicStroke(3.0f)
                        );

                        g2.drawLine(
                            p.x,
                            p.y,
                            next.x,
                            next.y
                        );
                    }
                }
            }
        }

        /*
         * =========================
         * セル
         * =========================
         */

        g2.setColor(Color.BLACK);

        for (int z = 0; z < sizeZ; z++) {

            for (int y = 0; y < sizeY; y++) {

                for (int x = 0; x < sizeX; x++) {

                    Point p =
                        project(x, y, z);

                    g2.fillOval(
                        p.x - CELL_POINT_SIZE / 2,
                        p.y - CELL_POINT_SIZE / 2,
                        CELL_POINT_SIZE,
                        CELL_POINT_SIZE
                    );
                }
            }
        }
    }

    @Override
    public void drawPlayer(Graphics g) {

        Graphics2D g2 =
            (Graphics2D) g;

        updateProjection(g2);

        int x =
            player.pos.coords[0];

        int y =
            player.pos.coords.length >= 2
                ? player.pos.coords[1]
                : 0;

        int z =
            player.pos.coords.length >= 3
                ? player.pos.coords[2]
                : 0;

        Point p =
            project(x, y, z);

        /*
         * プレイヤー
         */
        g2.setColor(Color.RED);

        g2.fillOval(
            p.x - PLAYER_POINT_SIZE / 2,
            p.y - PLAYER_POINT_SIZE / 2,
            PLAYER_POINT_SIZE,
            PLAYER_POINT_SIZE
        );

        g2.setColor(Color.BLACK);

        g2.drawOval(
            p.x - PLAYER_POINT_SIZE / 2,
            p.y - PLAYER_POINT_SIZE / 2,
            PLAYER_POINT_SIZE,
            PLAYER_POINT_SIZE
        );
    }

    /*
     * =========================
     * 回転
     * =========================
     */

    public void rotateLeft() {
        yaw -= ROTATE_STEP;
        yaw = normalizeAngle(yaw);
    }

    public void rotateRight() {
        yaw += ROTATE_STEP;
        yaw = normalizeAngle(yaw);
    }

    public void rotateUp() {

        pitch += ROTATE_STEP;

        if (pitch > 90.0) {
            pitch = 90.0;
        }
    }

    public void rotateDown() {

        pitch -= ROTATE_STEP;

        if (pitch < -90.0) {
            pitch = -90.0;
        }
    }

    private double normalizeAngle(
            double angle) {

        angle %= 360.0;

        if (angle < 0) {
            angle += 360.0;
        }

        return angle;
    }

    /*
     * =========================
     * 投影
     * =========================
     */

    private Point project(
            int x,
            int y,
            int z) {

        double[] point =
            rotatePoint(
                x,
                y,
                z
            );

        int screenX =
            (int) Math.round(
                point[0] * scale
                    + offsetX
            );

        int screenY =
            (int) Math.round(
                -point[1] * scale
                    + offsetY
            );

        return new Point(
            screenX,
            screenY
        );
    }

    /*
     * 3D回転
     */
    private double[] rotatePoint(
            double x,
            double y,
            double z) {

        /*
         * 迷路中心
         */
        double centerX =
            (maze.mazeSize[0] - 1)
                / 2.0;

        double centerY =
            maze.mazeSize.length >= 2
                ? (maze.mazeSize[1] - 1)
                    / 2.0
                : 0;

        double centerZ =
            maze.mazeSize.length >= 3
                ? (maze.mazeSize[2] - 1)
                    / 2.0
                : 0;

        /*
         * 中心を原点へ
         */
        x -= centerX;
        y -= centerY;
        z -= centerZ;

        /*
         * 左右回転
         * Z軸回転
         */
        double yawRad =
            Math.toRadians(yaw);

        double x1 =
            x * Math.cos(yawRad)
                - y * Math.sin(yawRad);

        double y1 =
            x * Math.sin(yawRad)
                + y * Math.cos(yawRad);

        double z1 = z;

        /*
         * 上下回転
         * X軸回転
         */
        double pitchRad =
            Math.toRadians(pitch);

        double x2 = x1;

        double y2 =
            y1 * Math.cos(pitchRad)
                - z1 * Math.sin(pitchRad);

        double z2 =
            y1 * Math.sin(pitchRad)
                + z1 * Math.cos(pitchRad);

        /*
         * x2 = 横
         * z2 = 縦
         * y2 = 奥行き
         */
        return new double[] {
            x2,
            z2,
            y2
        };
    }

    /*
     * =========================
     * 自動拡大縮小
     * =========================
     */

    private void updateProjection(
            Graphics2D g2) {

        int sizeX =
            maze.mazeSize[0];

        int sizeY =
            maze.mazeSize.length >= 2
                ? maze.mazeSize[1]
                : 1;

        int sizeZ =
            maze.mazeSize.length >= 3
                ? maze.mazeSize[2]
                : 1;

        double minX =
            Double.MAX_VALUE;

        double maxX =
            -Double.MAX_VALUE;

        double minY =
            Double.MAX_VALUE;

        double maxY =
            -Double.MAX_VALUE;

        for (int z = 0; z < sizeZ; z++) {

            for (int y = 0; y < sizeY; y++) {

                for (int x = 0; x < sizeX; x++) {

                    double[] point =
                        rotatePoint(
                            x,
                            y,
                            z
                        );

                    double rawX =
                        point[0];

                    double rawY =
                        -point[1];

                    minX =
                        Math.min(
                            minX,
                            rawX
                        );

                    maxX =
                        Math.max(
                            maxX,
                            rawX
                        );

                    minY =
                        Math.min(
                            minY,
                            rawY
                        );

                    maxY =
                        Math.max(
                            maxY,
                            rawY
                        );
                }
            }
        }

        Rectangle clip =
            g2.getClipBounds();

        int width;
        int height;

        if (clip != null) {

            width =
                clip.width;

            height =
                clip.height;

        } else {

            width =
                GameSetting.WINDOW_WIDTH;

            height =
                GameSetting.WINDOW_HEIGHT;
        }

        /*
         * ボタン用に少し余白を取る
         */
        int controlSpace = 65;

        double mazeWidth =
            Math.max(
                1.0,
                maxX - minX
            );

        double mazeHeight =
            Math.max(
                1.0,
                maxY - minY
            );

        double scaleX =
            (width - MARGIN * 2.0)
                / mazeWidth;

        double scaleY =
            (
                height
                - MARGIN * 2.0
                - controlSpace
            )
                / mazeHeight;

        scale =
            Math.min(
                scaleX,
                scaleY
            );

        scale =
            Math.min(
                scale,
                40.0
            );

        double drawWidth =
            mazeWidth * scale;

        double drawHeight =
            mazeHeight * scale;

        offsetX =
            (width - drawWidth)
                / 2.0
                - minX * scale;

        offsetY =
            (
                height
                - controlSpace
                - drawHeight
            )
                / 2.0
                - minY * scale;
    }
}