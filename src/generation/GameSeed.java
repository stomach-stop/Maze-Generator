package generation;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** 6文字の36進数で表す、再現用のシード。 */
public final class GameSeed {
    private static final long LIMIT = 2176782336L; // 36^6
    private final long value;

    private GameSeed(long value) { this.value = value; }

    public static GameSeed fromSetting(String text) {
        String normalized = text == null ? "" : text.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) return new GameSeed(ThreadLocalRandom.current().nextLong(LIMIT));
        if (!normalized.matches("[0-9A-Z]{1,6}")) {
            throw new IllegalArgumentException("シードは英数字1〜6文字で指定してください。");
        }
        return new GameSeed(Long.parseLong(normalized, 36));
    }

    public long value() { return value; }
    public String code() {
        return String.format(Locale.ROOT, "%6s", Long.toString(value, 36).toUpperCase(Locale.ROOT))
                .replace(' ', '0');
    }
}
