package game;

import game.generator.AldousBroder;
import game.generator.Generator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/** アプリの起動、非同期生成、共有モデルとウィンドウの寿命を管理する。 */
public final class Game extends JFrame {
    private static final long serialVersionUID = 1L;
    private final List<GamePanel> panels = new ArrayList<>();
    private Maze maze;
    private Player player;

    public Game() { this(AldousBroder::new); }

    /** アルゴリズムや乱数シードの選択を外部から渡せる入口。 */
    public Game(Function<Maze, Generator> generatorFactory) {
        super("Maze Generator");
        int[] sizes = GameSetting.MAZE_SIZE.clone();
        Position start = new Position(GameSetting.START_POSITION);
        JLabel status = new JLabel("迷路を生成中...", JLabel.CENTER);
        status.setPreferredSize(GameSetting.preferredViewSize());
        add(status, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(360, 320));
        setLocationRelativeTo(null);
        setVisible(true);

        new SwingWorker<Maze, Void>() {
            @Override
            protected Maze doInBackground() {
                if (sizes.length != 3) throw new IllegalArgumentException("The viewer requires a 3D maze.");
                Maze generated = new Maze(sizes);
                generatorFactory.apply(generated).generate(start);
                return generated;
            }

            @Override
            protected void done() {
                try {
                    maze = get();
                    player = new Player(maze, start);
                    GamePanel panel = createPanel();
                    setContentPane(panel);
                    revalidate();
                    repaint();
                    panel.requestFocusInWindow();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    status.setText("迷路の生成が中断されました。");
                } catch (ExecutionException exception) {
                    status.setText("迷路を生成できませんでした: " + exception.getCause().getMessage());
                }
            }
        }.execute();
    }

    private GamePanel createPanel() {
        GamePanel panel = new GamePanel(maze, player);
        new InputHandler(player, panel::setView, panel::activeCamera,
                this::openWindow, this::repaintAllPanels).install(panel);
        panels.add(panel);
        return panel;
    }

    public void openWindow() {
        if (maze == null || player == null) return;
        GamePanel panel = createPanel();
        JFrame frame = new JFrame("Maze");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(360, 320));
        frame.setLocationByPlatform(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) { panels.remove(panel); }
        });
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    private void repaintAllPanels() {
        for (GamePanel panel : panels) panel.repaint();
    }
}
