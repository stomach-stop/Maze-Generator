package game;

import javax.swing.JFrame;
import javax.swing.Timer;
import game.generator.*;

public class Game extends JFrame {
    private Generator generator;
    private GamePanel panel;
    private InputHandler handler;
    private Maze maze;
    private Player player;
    
    public Game() {
        setTitle("Maze Generator");
        setSize(
            GameSetting.WINDOW_WIDTH,
            GameSetting.WINDOW_HEIGHT
        );

        maze = new Maze(GameSetting.MAZE_SIZE);

        //generator = new RecursiveBacktracker(maze);
        //generator = new Kruskal(maze);
        //generator = new Prim(maze);
        generator = new AldousBroder(maze);
        //generator = new Wilson(maze);

        generator.generate(
            new Position(GameSetting.START_POSITION)
        );

        player = new Player(maze);
        panel = new GamePanel(maze, player);
        handler = new InputHandler(panel, player, this);

        panel.addKeyListener(handler);
        panel.setFocusable(true);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        panel.requestFocusInWindow();
    }

    public void openWindow() {
        JFrame frame = new JFrame("Maze");

        GamePanel newPanel = new GamePanel(maze, player);
        InputHandler newHandler =
            new InputHandler(newPanel, player, this);

        newPanel.addKeyListener(newHandler);
        newPanel.setFocusable(true);

        frame.add(newPanel);

        frame.setSize(
            GameSetting.WINDOW_WIDTH,
            GameSetting.WINDOW_HEIGHT
        );

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        newPanel.requestFocusInWindow();

        // 追加画面の描画を更新
        Timer timer = new Timer(50, e -> {
            if (!frame.isDisplayable()) {
                ((Timer) e.getSource()).stop();
                return;
            }
            newPanel.repaint();
        });

        timer.start();
    }
}