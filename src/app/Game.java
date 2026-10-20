package app;

import generation.GameSeed;
import generation.MazeAlgorithm;
import input.InputHandler;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import model.Maze;
import model.Player;
import model.Position;
import session.GameTimer;
import session.GameConfig;
import view.GamePanel;
import view.ResultPanel;
import view.SettingsPanel;
import view.ViewSettings;

/** 非同期生成、プレイ、リザルトの遷移と共有ウィンドウを管理する。 */
public final class Game extends JFrame {
    private static final long serialVersionUID = 1L;
    private final GameTimer timer = new GameTimer();
    private GameConfig runConfig;
    private final List<GamePanel> panels = new ArrayList<>();
    private final List<JFrame> extraWindows = new ArrayList<>();
    private Maze maze;
    private Player player;
    private boolean playing;
    private boolean generating;

    public Game() { this(GameSetting.ALGORITHM); }

    public Game(MazeAlgorithm algorithm) {
        super("Maze Generator");
        GameConfig initial = new GameConfig(algorithm, GameSetting.SEED,
                GameSetting.MAZE_SIZE, new Position(GameSetting.START_POSITION));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(ViewSettings.preferredViewSize());
        pack();
        setMinimumSize(new Dimension(360, 320));
        setLocationRelativeTo(null);
        setContentPane(new SettingsPanel(initial, this::startNewGame));
        revalidate();
        setVisible(true);
    }

    private void startNewGame(GameConfig requested) {
        if (generating) return;
        GameConfig config = requested.resolveSeed();
        playing = false;
        timer.stop();
        generating = true;
        closeExtraWindows();
        panels.clear();
        maze = null;
        player = null;
        int[] sizes = config.sizes();
        Position start = config.start();
        JLabel status = new JLabel("迷路を生成中...", JLabel.CENTER);
        setContentPane(status);
        revalidate();
        repaint();

        new SwingWorker<Maze, Void>() {
            @Override
            protected Maze doInBackground() {
                if (sizes.length != 3) throw new IllegalArgumentException("The viewer requires a 3D maze.");
                GameSeed seed = GameSeed.fromSetting(config.seed());
                Maze generated = new Maze(sizes);
                config.algorithm().create(generated, seed.value()).generate(start);
                return generated;
            }

            @Override
            protected void done() {
                generating = false;
                try {
                    maze = get();
                    runConfig = config;
                    player = new Player(maze, start);
                    playing = true;
                    GamePanel panel = createPanel();
                    setContentPane(panel);
                    revalidate();
                    repaint();
                    panel.requestFocusInWindow();
                    // 生成・UI準備を除外し、迷路の表示を済ませてから計測する。
                    panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
                    timer.start();
                    // 1×1×1など、開始時に既に終点にいる場合にも対応する。
                    afterInput();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showSettingsError(requested, "迷路の生成が中断されました。");
                } catch (ExecutionException | RuntimeException exception) {
                    playing = false;
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    showSettingsError(requested, "迷路を生成できませんでした: " + cause.getMessage());
                }
            }
        }.execute();
    }

    private void showSettingsError(GameConfig config, String message) {
        playing = false;
        timer.stop();
        SettingsPanel settings = new SettingsPanel(config, this::startNewGame);
        settings.showError(message);
        setContentPane(settings);
        revalidate();
        repaint();
    }

    private GamePanel createPanel() {
        Player sessionPlayer = player;
        GamePanel panel = new GamePanel(maze, player, () -> {
            if (!playing || player != sessionPlayer) return;
            player.resetPosition();
            followPlayerLayers();
            afterInput();
        }, () -> {
            if (!playing || player != sessionPlayer) return;
            player.toggleTrailVisibility();
            afterInput();
        });
        new InputHandler(player, panel::setView, panel::activeCamera,
                this::openWindow, this::afterInput, this::followPlayerLayers,
                () -> playing && player == sessionPlayer).install(panel);
        panels.add(panel);
        return panel;
    }

    private void afterInput() {
        if (!playing) return;
        if (player.hasReachedGoal()) {
            showResult();
            return;
        }
        for (GamePanel panel : panels) {
            panel.refreshControls();
            panel.repaint();
        }
    }

    private void showResult() {
        timer.stop();
        playing = false;
        closeExtraWindows();
        panels.clear();
        GameConfig completed = runConfig;
        setContentPane(new ResultPanel(completed, timer.elapsedMillis(), player.moveCount(),
                this::startNewGame, () -> startNewGame(completed)));
        revalidate();
        repaint();
        toFront();
    }

    private void closeExtraWindows() {
        for (JFrame frame : new ArrayList<>(extraWindows)) frame.dispose();
        extraWindows.clear();
    }

    public void openWindow() {
        if (!playing) return;
        GamePanel panel = createPanel();
        JFrame frame = new JFrame("Maze");
        extraWindows.add(frame);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(360, 320));
        frame.setLocationByPlatform(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) {
                panels.remove(panel);
                extraWindows.remove(frame);
            }
        });
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    private void followPlayerLayers() {
        for (GamePanel panel : panels) panel.followPlayerLayer();
    }
}
