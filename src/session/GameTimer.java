package session;


/** システム時計の時刻変更に影響されない、1プレイ分の計測。 */
public final class GameTimer {
    private long startedAt;
    private long elapsedNanos;
    private boolean running;

    public void start() {
        elapsedNanos = 0;
        startedAt = System.nanoTime();
        running = true;
    }

    public void stop() {
        if (!running) return;
        elapsedNanos = System.nanoTime() - startedAt;
        running = false;
    }

    public long elapsedMillis() {
        return (running ? System.nanoTime() - startedAt : elapsedNanos) / 1_000_000;
    }
}
