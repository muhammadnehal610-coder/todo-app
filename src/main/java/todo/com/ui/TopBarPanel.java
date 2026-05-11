package todo.com.ui;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

public class TopBarPanel extends JPanel {
    private final JComboBox<String> priorityFilterDropdown;
    private final JComboBox<String> statusFilterDropdown;
    private final JComboBox<String> dateFilterDropdown;
    private final JButton addTaskBtn;

    public TopBarPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(5, 0, 20, 0));

        JLabel title = new JLabel("Task Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        priorityFilterDropdown = new JComboBox<>(new String[]{"All Priority", "High", "Medium", "Low"});
        priorityFilterDropdown.setPreferredSize(new Dimension(130, 35));

        statusFilterDropdown = new JComboBox<>(new String[]{"All Status", "Pending", "Done"});
        statusFilterDropdown.setPreferredSize(new Dimension(120, 35));

        dateFilterDropdown = new JComboBox<>(new String[]{"All Dates", "Today", "Yesterday", "Tomorrow"});
        dateFilterDropdown.setPreferredSize(new Dimension(130, 35));

        addTaskBtn = new JButton("Add New Task");
        addTaskBtn.setPreferredSize(new Dimension(150, 35));
        addTaskBtn.setFocusPainted(false);
        addTaskBtn.setBackground(new Color(0, 123, 255));
        addTaskBtn.setForeground(Color.WHITE);
        addTaskBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(title, BorderLayout.WEST);
        add(createActionsPanel(), BorderLayout.EAST);
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        filterLabel.setForeground(Color.GRAY);

        actionsPanel.add(filterLabel);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(priorityFilterDropdown);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(dateFilterDropdown);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(statusFilterDropdown);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(addTaskBtn);
        return actionsPanel;
    }

    public void setAddTaskListener(ActionListener listener) {
        addTaskBtn.addActionListener(listener);
    }

    public void setFilterChangeListener(FilterChangeListener listener) {
        ActionListener actionListener = e -> listener.onFiltersChanged(
                getSelectedPriority(),
                getSelectedDateFilter(),
                getSelectedStatus()
        );

        priorityFilterDropdown.addActionListener(actionListener);
        statusFilterDropdown.addActionListener(actionListener);
        dateFilterDropdown.addActionListener(actionListener);
    }

    public void clearFilters() {
        priorityFilterDropdown.setSelectedItem("All Priority");
        dateFilterDropdown.setSelectedItem("All Dates");
        statusFilterDropdown.setSelectedItem("All Status");
    }

    private String getSelectedPriority() {
        return priorityFilterDropdown.getSelectedItem().toString();
    }

    private String getSelectedDateFilter() {
        return dateFilterDropdown.getSelectedItem().toString();
    }

    private String getSelectedStatus() {
        return statusFilterDropdown.getSelectedItem().toString();
    }

    public interface FilterChangeListener {
        void onFiltersChanged(String priority, String dateFilter, String status);
    }
}
