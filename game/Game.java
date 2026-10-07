package game;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import game.generator.*;

public class Game extends JFrame {
    private Generator generator;
    private GamePanel panel;
    private InputHandler handler;
    private Maze maze;
    private Player player;
    private List<GamePanel> panels = new ArrayList<>();
    
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
        panels.add(panel);
        handler = new InputHandler(panel, player, this);

        panel.addKeyListener(handler);
        panel.setFocusable(true);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        panel.requestFocusInWindow();
    }

    //以下AI
    public void openWindow() {

        JFrame frame =
            new JFrame("Maze");


        GamePanel newPanel =
            new GamePanel(
                maze,
                player
            );

        /*
         * 全画面リストへ登録
         */
        panels.add(newPanel);


        InputHandler newHandler =
            new InputHandler(
                newPanel,
                player,
                this
            );


        newPanel.addKeyListener(
            newHandler
        );

        newPanel.setFocusable(
            true
        );


        frame.add(
            newPanel
        );


        frame.setSize(
            GameSetting.WINDOW_WIDTH,
            GameSetting.WINDOW_HEIGHT
        );


        frame.setDefaultCloseOperation(
            JFrame.DISPOSE_ON_CLOSE
        );

        frame.setLocationByPlatform(
            true
        );

        setVisibleWindow(
            frame,
            newPanel
        );
    }


    /*
     * ウィンドウ表示処理
     */
    private void setVisibleWindow(
            JFrame frame,
            GamePanel newPanel) {

        frame.setVisible(
            true
        );

        newPanel.requestFocusInWindow();


        /*
         * ウィンドウが閉じられたら
         * panelsから削除
         */
        frame.addWindowListener(
            new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosed(
                        java.awt.event.WindowEvent e) {

                    panels.remove(
                        newPanel
                    );
                }
            }
        );
    }


    /*
     * =========================
     * 全画面再描画
     * =========================
     */

    public void repaintAllPanels() {

        for (
            GamePanel gamePanel
                : panels
        ) {

            gamePanel.repaint();
        }
    }
}