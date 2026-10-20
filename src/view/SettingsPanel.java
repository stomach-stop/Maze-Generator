package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import session.GameConfig;
import view.widget.GameSettingsForm;

/** 設定とリザルトの共通レイアウト。右側の設定欄と下部の操作位置を固定する。 */
public class SettingsPanel extends JPanel {
    private final JLabel error = new JLabel(" ");

    public SettingsPanel(GameConfig initial, Consumer<GameConfig> startNew) {
        this("迷路の設定", initial,
                new JLabel("右側で設定し、下のボタンで開始してください。", SwingConstants.CENTER),
                startNew, null);
    }

    protected SettingsPanel(String title, GameConfig initial, JComponent details,
                            Consumer<GameConfig> startNew, Runnable replay) {
        super(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 24f));
        add(heading, BorderLayout.NORTH);
        GameSettingsForm form = new GameSettingsForm(initial);
        JPanel columns = new JPanel(new GridLayout(1, 2, 24, 0));
        columns.add(details);
        columns.add(form);
        JScrollPane scroll = new JScrollPane(columns);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        error.setForeground(new Color(180, 30, 30));
        footer.add(error, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(1, 2, 24, 0));
        JButton newButton = new JButton("新しくスタート");
        newButton.addActionListener(event -> {
            try { startNew.accept(form.readConfig()); }
            catch (IllegalArgumentException exception) { showError(exception.getMessage()); }
        });
        buttons.add(newButton);
        if (replay == null) {
            buttons.add(new JPanel());
        } else {
            JButton sameButton = new JButton("同じでスタート");
            sameButton.setToolTipText("右側の変更に関係なく、直前と同じ迷路で開始します。");
            sameButton.addActionListener(event -> replay.run());
            buttons.add(sameButton);
        }
        footer.add(buttons, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    public void showError(String message) { error.setText(message); }
}
