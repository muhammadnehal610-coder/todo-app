package todo.com.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private TaskTablePanel taskTablePanel;
    private TopBarPanel topBarPanel;

    public MainWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setTitle("My To-Do App - MongoDB");

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        // Initialize components
        topBarPanel = new TopBarPanel();
        taskTablePanel = new TaskTablePanel();

        // Wire up the add task button
        topBarPanel.setAddTaskListener(e -> {
            AddTaskDialog dialog = new AddTaskDialog(this, null, false);
            dialog.setVisible(true);
        });

        mainPanel.add(topBarPanel, BorderLayout.NORTH);
        mainPanel.add(taskTablePanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    public TaskTablePanel getTaskTablePanel() {
        return taskTablePanel;
    }

    public TopBarPanel getTopBarPanel() {
        return topBarPanel;
    }
}
