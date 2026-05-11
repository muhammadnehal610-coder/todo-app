package todo.com.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.MatteBorder;
import java.awt.Color;

public final class RowStyle {
    public static final Color COLOR_HOVER_SELECT = Color.LIGHT_GRAY;
    public static final Color COLOR_STRIPE = new Color(248, 249, 250);
    private static final MatteBorder ROW_BOTTOM_BORDER = new MatteBorder(0, 0, 1, 0, Color.DARK_GRAY);

    private RowStyle() {
    }

    public static void apply(JComponent comp, int row, boolean isSelected, int hoveredRow) {
        if (isSelected || row == hoveredRow) {
            comp.setBackground(COLOR_HOVER_SELECT);
        } else {
            comp.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
        }

        comp.setBorder(BorderFactory.createCompoundBorder(
                ROW_BOTTOM_BORDER,
                BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));

        comp.setForeground(Color.BLACK);
        comp.setOpaque(true);
    }
}
