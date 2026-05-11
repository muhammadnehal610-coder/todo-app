package todo.com.ui;

import todo.com.database.MongoDBManager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

public class MainWindow extends JFrame {
    private final TaskTablePanel taskTablePanel;
    private final TopBarPanel topBarPanel;

    public MainWindow(MongoDBManager dbManager) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setTitle("To-Do App");

        Image appIcon = AppIcons.getAppIconImage();
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        taskTablePanel = new TaskTablePanel(this, dbManager);
        topBarPanel = new TopBarPanel();

        topBarPanel.setAddTaskListener(e -> {
            AddTaskDialog dialog = new AddTaskDialog(this, null, false, this::showAllTasks, null);
            dialog.setVisible(true);
        });
        topBarPanel.setFilterChangeListener(taskTablePanel::applyFilters);

        mainPanel.add(topBarPanel, BorderLayout.NORTH);
        mainPanel.add(taskTablePanel, BorderLayout.CENTER);
        add(mainPanel);

        showDatabaseConnectionStatus(dbManager);
    }

    public TaskTablePanel getTaskTablePanel() {
        return taskTablePanel;
    }

    public TopBarPanel getTopBarPanel() {
        return topBarPanel;
    }

    private void showAllTasks() {
        topBarPanel.clearFilters();
        taskTablePanel.loadDataFromMongoDB();
    }

    private void showDatabaseConnectionStatus(MongoDBManager dbManager) {
        if (dbManager.isConnected()) {
            return;
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                "MongoDB connection failed.\n\n"
                        + "Please check your internet connection, MongoDB Atlas IP access list, "
                        + "database user/password, and cluster status.\n\n"
                        + "Details: " + dbManager.getConnectionError(),
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE
        ));
    }
}
