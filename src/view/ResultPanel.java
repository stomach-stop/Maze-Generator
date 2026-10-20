package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import session.GameConfig;

/** 共通の設定画面に、直前の結果と同じ迷路の再開ボタンを追加する。 */
public final class ResultPanel extends SettingsPanel {
    public ResultPanel(GameConfig completed, long elapsedMillis, long moves,
                       Consumer<GameConfig> startNew, Runnable replay) {
        super("ゴール！ 迷路クリア", completed.withSeed(""),
                resultDetails(completed, elapsedMillis, moves), startNew, replay);
    }

    private static JPanel resultDetails(GameConfig config, long elapsedMillis, long moves) {
        JPanel container = new JPanel(new BorderLayout());
        JPanel details = new JPanel(new GridLayout(0, 1, 0, 12));
        details.setBorder(BorderFactory.createTitledBorder("今回の結果"));
        details.add(new JLabel("生成方式：" + config.algorithm().displayName()));
        details.add(new JLabel("迷路サイズ：" + Arrays.toString(config.sizes())));
        details.add(new JLabel("開始位置：" + Arrays.toString(config.start().toArray())));
        JPanel seedRow = new JPanel(new BorderLayout(8, 0));
        seedRow.add(new JLabel("シード："), BorderLayout.WEST);
        JTextField seedText = new JTextField(config.seed());
        seedText.setEditable(false);
        seedRow.add(seedText, BorderLayout.CENTER);
        details.add(seedRow);
        details.add(new JLabel(String.format(Locale.ROOT, "クリア時間：%d分 %02d.%03d秒",
                elapsedMillis / 60000, elapsedMillis / 1000 % 60, elapsedMillis % 1000)));
        details.add(new JLabel("移動回数：" + moves + " 回"));
        container.add(details, BorderLayout.NORTH);
        return container;
    }
}
