package todo.com.ui;

import todo.com.database.MongoDBManager;

import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class TableCellEditorImplementation extends AbstractCellEditor implements TableCellEditor {
    private final ActionPanel panel;
    private final JFrame frame;
    private final JTable table;
    private final Runnable refreshTasks;
    private int editingRow;

    public TableCellEditorImplementation(JFrame frame, JTable table, Runnable refreshTasks) {
        this.frame = frame;
        this.table = table;
        this.refreshTasks = refreshTasks;
        this.panel = new ActionPanel();

        panel.btnDone.addActionListener(e -> markTaskDone());
        panel.btnEdit.addActionListener(e -> editTask());
        panel.btnDelete.addActionListener(e -> deleteTask());
    }

    private void markTaskDone() {
        if (editingRow >= 0) {
            stopCellEditing();
            String taskId = (String) table.getValueAt(editingRow, 0);
            MongoDBManager.getInstance().updateTaskStatus(taskId, "Done");
            refreshTasks.run();
            table.repaint();
        }
    }

    private void editTask() {
        if (editingRow >= 0) {
            stopCellEditing();
            String taskId = (String) table.getValueAt(editingRow, 0);
            String status = (String) table.getValueAt(editingRow, 5);
            AddTaskDialog dialog = new AddTaskDialog(frame, taskId, true, refreshTasks, status);
            dialog.setVisible(true);
        }
    }

    private void deleteTask() {
        if (editingRow >= 0) {
            stopCellEditing();
            String taskId = (String) table.getValueAt(editingRow, 0);
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (MongoDBManager.getInstance().deleteTask(taskId)) {
                    refreshTasks.run();
                    table.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete task", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.editingRow = row;
        RowStyle.apply(panel, row, true, -1);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}
