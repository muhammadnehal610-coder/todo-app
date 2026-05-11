package todo.com.ui;

import org.bson.Document;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import todo.com.database.MongoDBManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.Properties;

public class AddTaskDialog extends JDialog {
    private final JTextField txtTitle = new JTextField();
    private final JTextArea txtDescription = new JTextArea();
    private final JTextField txtCategory = new JTextField();
    private final JComboBox<String> cbPriority = new JComboBox<>(new String[]{"High", "Medium", "Low"});
    private final JButton btnSave = new JButton();

    private final MongoDBManager dbManager;
    private final String taskId;
    private final boolean isEditMode;
    private final Runnable refreshTasks;
    private final String currentStatus;

    private JDatePickerImpl datePicker;

    public AddTaskDialog(java.awt.Frame parent, String taskId, boolean isEditMode,
                         Runnable refreshTasks, String currentStatus) {
        super(parent, isEditMode ? "Edit Task" : "Add Task", true);

        this.dbManager = MongoDBManager.getInstance();
        this.taskId = taskId;
        this.isEditMode = isEditMode;
        this.refreshTasks = refreshTasks;
        this.currentStatus = currentStatus;

        Image appIcon = AppIcons.getAppIconImage();
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        initializeDatePicker();
        initializeUI();

        if (isEditMode && taskId != null) {
            loadTaskData(taskId);
        }
    }

    private void initializeDatePicker() {
        UtilDateModel dateModel = new UtilDateModel();
        dateModel.setSelected(true);

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setPreferredSize(new Dimension(200, 38));
    }

    private void initializeUI() {
        setSize(520, 600);
        setMinimumSize(new Dimension(520, 520));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(java.awt.Color.WHITE);

        container.add(createForm(), BorderLayout.CENTER);
        container.add(createFooter(), BorderLayout.SOUTH);
        add(container);

        btnSave.addActionListener(e -> saveTask());
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(java.awt.Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        Dimension fieldSize = new Dimension(300, 38);
        txtTitle.setPreferredSize(fieldSize);
        txtCategory.setPreferredSize(fieldSize);
        cbPriority.setPreferredSize(fieldSize);

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setPreferredSize(new Dimension(300, 120));

        addFormRow(form, gbc, 0, "Title", txtTitle);
        addFormRow(form, gbc, 1, "Description", descriptionScroll);
        addFormRow(form, gbc, 2, "Category", txtCategory);
        addFormRow(form, gbc, 3, "Priority", cbPriority);
        addFormRow(form, gbc, 4, "Due Date", datePicker);

        return form;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = "Description".equals(label) ? GridBagConstraints.NORTH : GridBagConstraints.CENTER;
        form.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(java.awt.Color.WHITE);

        btnSave.setText(isEditMode ? "Update Task" : "Create Task");
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(140, 40));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(140, 40));
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnCancel);
        footer.add(btnSave);
        return footer;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private void saveTask() {
        String title = txtTitle.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required!");
            return;
        }

        String description = txtDescription.getText().trim();
        String category = txtCategory.getText().trim();
        String priority = cbPriority.getSelectedItem().toString();
        String dueDate = datePicker.getJFormattedTextField().getText();

        try {
            boolean success = isEditMode
                    ? dbManager.updateTask(taskId, title, category, priority, dueDate, description, currentStatus)
                    : dbManager.addTask(title, category, priority, dueDate, description) != null;

            if (success) {
                refreshTasks.run();
                dispose();
                JOptionPane.showMessageDialog(getParent(), isEditMode ? "Task updated successfully!" : "Task created successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Failed to update task" : "Failed to create task",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTaskData(String taskId) {
        Document task = dbManager.getTaskById(taskId);
        if (task != null) {
            txtTitle.setText(task.getString("title"));
            txtCategory.setText(task.getString("category"));
            cbPriority.setSelectedItem(task.getString("priority"));
            datePicker.getJFormattedTextField().setText(task.getString("dueDate"));
            txtDescription.setText(task.getString("description"));
        }
    }
}
