package renderer;

import game.Cell;
import game.Direction;
import game.GameSetting;
import game.Maze;
import game.Player;
import game.Position;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import renderer.OrbitCamera.ViewPoint;

/** スケルトンの形状と描画順序を担当する。 */
public final class SkeletonRender implements MazeRender {
    private final Maze maze;
    private final Player player;
    private final OrbitCamera camera;

    public SkeletonRender(Maze maze, Player player, OrbitCamera camera) {
        this.maze = maze;
        this.player = player;
        this.camera = camera;
    }

    public static double boundingRadius(Maze maze) {
        double squared = 0;
        for (int axis = 0; axis < maze.dimensions(); axis++) {
            double span = maze.size(axis) - 1;
            squared += span * span;
        }
        return Math.sqrt(squared) * GameSetting.SKELETON_CELL_SPACING * 0.5
                + GameSetting.SKELETON_PLAYER_RADIUS;
    }

    @Override
    public void render(Graphics2D graphics, int width, int height) {
        camera.setViewportSize(width, height);
        drawMaze(graphics);
        drawPlayer(graphics);
    }
    private void drawMaze(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            List<Primitive> scene = new ArrayList<>();
            int sizeX = maze.size(0);
            int sizeY = maze.dimensions() > 1 ? maze.size(1) : 1;
            int sizeZ = maze.dimensions() > 2 ? maze.size(2) : 1;

            for (int z = 0; z < sizeZ; z++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int x = 0; x < sizeX; x++) {
                        Position position = position(x, y, z);
                        Cell cell = maze.cellAt(position);
                        ViewPoint from = toView(x, y, z);
                        scene.add(Primitive.sphere(from));

                        if (x + 1 < sizeX && !cell.hasWall(Direction.PositiveX)) {
                            scene.add(Primitive.edge(from, toView(x + 1, y, z)));
                        }
                        if (y + 1 < sizeY && !cell.hasWall(Direction.PositiveY)) {
                            scene.add(Primitive.edge(from, toView(x, y + 1, z)));
                        }
                        if (z + 1 < sizeZ && !cell.hasWall(Direction.PositiveZ)) {
                            scene.add(Primitive.edge(from, toView(x, y, z + 1)));
                        }
                    }
                }
            }

            // Paint distant geometry first so nearer cells and passages cover it.
            scene.sort(Comparator.comparingDouble(Primitive::depth).reversed());
            for (Primitive primitive : scene) {
                if (primitive.edge) {
                    drawPassage(g, primitive);
                } else {
                    drawCell(g, primitive.from);
                }
            }
        } finally {
            g.dispose();
        }
    }

    private void drawPlayer(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int x = player.position().coordinate(0);
            int y = player.position().dimensions() > 1 ? player.position().coordinate(1) : 0;
            int z = player.position().dimensions() > 2 ? player.position().coordinate(2) : 0;
            ViewPoint point = toView(x, y, z);
            Point screen = camera.project(point);
            int radius = (int) Math.round(clamp(GameSetting.SKELETON_PLAYER_RADIUS * camera.focalLength() / point.depth, 2.0, 8.0));
            g.setColor(new Color(180, 35, 35));
            g.fillOval(screen.x - radius, screen.y - radius, radius * 2, radius * 2);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(screen.x - radius, screen.y - radius, radius * 2, radius * 2);
        } finally {
            g.dispose();
        }
    }

    private void drawPassage(Graphics2D g, Primitive primitive) {
        ViewPoint a = primitive.from;
        ViewPoint b = primitive.to;
        Point p1 = camera.project(a);
        Point p2 = camera.project(b);
        double depth = (a.depth + b.depth) * 0.5;
        float width = (float) clamp(GameSetting.SKELETON_PASSAGE_WIDTH * camera.focalLength() / depth, 0.6, 3.0);
        int shade = (int) clamp(145.0 - Math.max(0.0, depth - camera.distance()) / GameSetting.SKELETON_CELL_SPACING * 3.0, 65, 145);

        g.setColor(new Color(shade, shade, shade));
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(p1, p2));
        g.setColor(new Color(Math.min(220, shade + 45),
                Math.min(220, shade + 45), Math.min(220, shade + 45)));
        g.setStroke(new BasicStroke(Math.max(0.3f, width * 0.28f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(p1, p2));
    }

    private void drawCell(Graphics2D g, ViewPoint point) {
        Point center = camera.project(point);
        int radius = (int) Math.round(clamp(GameSetting.SKELETON_CELL_RADIUS * camera.focalLength() / point.depth, 1.0, 5.0));
        g.setPaint(new GradientPaint(center.x - radius, center.y - radius,
                new Color(250, 250, 250), center.x + radius, center.y + radius,
                new Color(65, 75, 90)));
        g.fillOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
        g.setColor(new Color(35, 40, 48));
        g.setStroke(new BasicStroke(1.0f));
        g.drawOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
    }

    private ViewPoint toView(double x, double y, double z) {
        double spacing = GameSetting.SKELETON_CELL_SPACING;
        x = (x - (maze.size(0) - 1) / 2.0) * spacing;
        y = (y - (maze.dimensions() > 1 ? maze.size(1) - 1 : 0) / 2.0) * spacing;
        z = (z - (maze.dimensions() > 2 ? maze.size(2) - 1 : 0) / 2.0) * spacing;
        return camera.toView(x, y, z);
    }

    private Position position(int x, int y, int z) {
        int[] coordinates = new int[maze.dimensions()];
        coordinates[0] = x;
        if (coordinates.length > 1) coordinates[1] = y;
        if (coordinates.length > 2) coordinates[2] = z;
        return new Position(coordinates);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    private static final class Primitive {
        final boolean edge;
        final ViewPoint from;
        final ViewPoint to;

        private Primitive(boolean edge, ViewPoint from, ViewPoint to) {
            this.edge = edge;
            this.from = from;
            this.to = to;
        }

        static Primitive sphere(ViewPoint point) {
            return new Primitive(false, point, point);
        }

        static Primitive edge(ViewPoint from, ViewPoint to) {
            return new Primitive(true, from, to);
        }

        double depth() {
            return (from.depth + to.depth) * 0.5;
        }
    }
}
