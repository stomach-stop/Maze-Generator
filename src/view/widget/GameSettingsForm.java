package view.widget;

import generation.MazeAlgorithm;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import model.Position;
import session.GameConfig;

/** 初期画面とリザルトで共有する、次のゲームの設定欄。 */
public final class GameSettingsForm extends JPanel {
    private final JComboBox<MazeAlgorithm> algorithm = new JComboBox<>(MazeAlgorithm.values());
    private final JTextField seed = new JTextField();
    private final JSpinner[] sizes = new JSpinner[3];
    private final Position start;

    public GameSettingsForm(GameConfig initial) {
        super(new BorderLayout());
        start = initial.start();
        setBorder(BorderFactory.createTitledBorder("次のゲームの設定"));
        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 8));
        fields.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        algorithm.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof MazeAlgorithm) setText(((MazeAlgorithm) value).displayName());
                return this;
            }
        });
        algorithm.setSelectedItem(initial.algorithm());
        fields.add(new JLabel("生成方法"));
        fields.add(algorithm);
        fields.add(new JLabel("シード（空欄ならランダム）"));
        seed.setText(initial.seed());
        fields.add(seed);
        JButton random = new JButton("ランダムにする（シードを空欄に）");
        random.addActionListener(event -> seed.setText(""));
        fields.add(random);
        int[] values = initial.sizes();
        String[] axes = {"X（幅）", "Y（奥行き）", "Z（高さ）"};
        for (int axis = 0; axis < 3; axis++) {
            sizes[axis] = new JSpinner(new SpinnerNumberModel(values[axis], 1, GameConfig.MAX_AXIS_SIZE, 1));
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.add(new JLabel(axes[axis]), BorderLayout.WEST);
            row.add(sizes[axis], BorderLayout.CENTER);
            fields.add(row);
        }
        fields.add(new JLabel("各軸 1〜" + GameConfig.MAX_AXIS_SIZE + " セル"));
        add(fields, BorderLayout.NORTH);
    }

    public GameConfig readConfig() {
        int[] values = new int[3];
        for (int axis = 0; axis < 3; axis++) {
            try { sizes[axis].commitEdit(); }
            catch (ParseException exception) {
                throw new IllegalArgumentException("サイズには範囲内の整数を入力してください。");
            }
            values[axis] = ((Number) sizes[axis].getValue()).intValue();
        }
        return new GameConfig((MazeAlgorithm) algorithm.getSelectedItem(), seed.getText(), values, start);
    }
}
