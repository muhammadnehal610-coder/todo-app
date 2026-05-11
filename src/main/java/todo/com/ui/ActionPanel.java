package todo.com.ui;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionPanel extends JPanel {
    final JButton btnDone;
    final JButton btnEdit;
    final JButton btnDelete;

    public ActionPanel() {
        Icon checkIcon = IconFontSwing.buildIcon(FontAwesome.CHECK_CIRCLE, 18, new Color(40, 167, 69));
        Icon editIcon = IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 18, new Color(0, 123, 255));
        Icon deleteIcon = IconFontSwing.buildIcon(FontAwesome.TRASH, 18, new Color(220, 53, 69));

        btnDone = new JButton(checkIcon);
        btnEdit = new JButton(editIcon);
        btnDelete = new JButton(deleteIcon);

        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        setOpaque(true);

        styleButton(btnDone);
        styleButton(btnEdit);
        styleButton(btnDelete);

        btnDone.setToolTipText("Mark as Done");
        btnEdit.setToolTipText("Edit Task");
        btnDelete.setToolTipText("Delete Task");

        add(btnDone);
        add(btnEdit);
        add(btnDelete);
    }

    private void styleButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 32));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setContentAreaFilled(true);
                btn.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
                btn.setBackground(null);
            }
        });
    }
}
