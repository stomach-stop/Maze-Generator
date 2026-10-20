package view.camera;

import java.awt.Point;

/** 物体中心を周回するカメラ。迷路やSwingのウィンドウに依存しない。 */
public final class OrbitCamera {
    private static final double MAX_PITCH = Math.toRadians(89);
    private final double minimumDistance;
    // XY表示を基準に、初期視点で+Xを右、+Yを下へ向ける。
    private double yaw = 0.0;
    private double pitch = Math.toRadians(-55);
    private double distance;
    private int viewportWidth = 1;
    private int viewportHeight = 1;

    public OrbitCamera(double boundingRadius, double clearance) {
        if (boundingRadius <= 0 || clearance <= 0) {
            throw new IllegalArgumentException("Camera bounds must be positive.");
        }
        minimumDistance = boundingRadius + clearance;
        distance = Math.max(minimumDistance, boundingRadius * 2.5);
    }

    public void setViewportSize(int width, int height) {
        viewportWidth = Math.max(1, width);
        viewportHeight = Math.max(1, height);
    }

    public void rotateByPixels(int dx, int dy) {
        yaw += dx * 0.008;
        pitch = Math.max(-MAX_PITCH, Math.min(MAX_PITCH, pitch - dy * 0.008));
    }

    public void zoom(double wheelRotation) {
        distance = Math.max(minimumDistance,
                Math.min(minimumDistance * 12, distance * Math.pow(1.12, wheelRotation)));
    }

    public ViewPoint rotateVector(double x, double y, double z) {
        // XYの+Y（画面下）を手前へ向け、+Zは上層方向として扱う。
        // 座標軸の表示もこの変換を共有し、モデル座標・移動処理は変更しない。
        y = -y;
        double horizontal = x * Math.cos(yaw) - y * Math.sin(yaw);
        double depthAxis = x * Math.sin(yaw) + y * Math.cos(yaw);
        return new ViewPoint(horizontal, z * Math.cos(pitch) - depthAxis * Math.sin(pitch),
                z * Math.sin(pitch) + depthAxis * Math.cos(pitch));
    }

    public ViewPoint toView(double x, double y, double z) {
        ViewPoint rotated = rotateVector(x, y, z);
        return new ViewPoint(rotated.x, rotated.y, Math.max(0.1, distance + rotated.depth));
    }

    public Point project(ViewPoint point) {
        double scale = focalLength() / point.depth;
        return new Point((int) Math.round(viewportWidth / 2.0 + point.x * scale),
                (int) Math.round(viewportHeight / 2.0 - point.y * scale));
    }

    public double focalLength() { return Math.max(1, Math.min(viewportWidth, viewportHeight) * 0.9); }
    public double distance() { return distance; }

    public static final class ViewPoint {
        public final double x;
        public final double y;
        public final double depth;

        private ViewPoint(double x, double y, double depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
    }
}
