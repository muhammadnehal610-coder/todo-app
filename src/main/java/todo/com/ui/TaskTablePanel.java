package todo.com.ui;

import org.bson.Document;
import todo.com.database.MongoDBManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskTablePanel extends JPanel {
    private final MongoDBManager dbManager;
    private final javax.swing.JFrame frame;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTable table;
    private int hoveredRow = -1;

    public TaskTablePanel(javax.swing.JFrame frame, MongoDBManager dbManager) {
        this.frame = frame;
        this.dbManager = dbManager;
        this.model = createTableModel();
        this.table = new JTable(model);
        this.sorter = new TableRowSorter<>(model);

        setLayout(new BorderLayout());
        setupTable();
        loadDataFromMongoDB();
        add(createScrollPane(), BorderLayout.CENTER);
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Title", "Category", "Priority", "Due Date", "Status", "Description", "Actions"};
        return new DefaultTableModel(new Object[0][0], columns) {
            public boolean isCellEditable(int row, int col) {
                return col == 7;
            }
        };
    }

    private void setupTable() {
        table.setRowSorter(sorter);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(RowStyle.COLOR_HOVER_SELECT);

        hideIdColumn();
        installHoverEffect();
        installCellRenderers();
        installActionColumn();
        styleHeader();
        installColumnResize();
    }

    private void hideIdColumn() {
        TableColumn idColumn = table.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);
    }

    private void installHoverEffect() {
        table.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }
        });
    }

    private void installCellRenderers() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                RowStyle.apply(this, row, isSelected, hoveredRow);
                setHorizontalAlignment((col == 2 || col == 3 || col == 5) ? CENTER : LEFT);
                return this;
            }
        });
    }

    private void installActionColumn() {
        TableColumn actionCol = table.getColumnModel().getColumn(7);

        actionCol.setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                ActionPanel panel = new ActionPanel();
                RowStyle.apply(panel, row, isSelected, hoveredRow);
                return panel;
            }
        });

        actionCol.setCellEditor(new TableCellEditorImplementation(frame, table, this::loadDataFromMongoDB));
    }

    private void styleHeader() {
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                javax.swing.JLabel label = new javax.swing.JLabel(value.toString());
                label.setOpaque(true);
                label.setFont(new Font("SansSerif", Font.BOLD, 13));
                label.setBackground(new Color(33, 33, 33));
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY),
                        new EmptyBorder(10, 15, 10, 15)
                ));
                label.setHorizontalAlignment((col == 2 || col == 3 || col == 5 || col == 7) ? CENTER : LEFT);
                return label;
            }
        });

        header.setReorderingAllowed(false);
        header.setBackground(new Color(33, 33, 33));
        header.setForeground(Color.WHITE);
    }

    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void installColumnResize() {
        Runnable resizeColumns = () -> {
            int w = table.getWidth();
            if (w == 0) {
                return;
            }

            TableColumnModel m = table.getColumnModel();
            m.getColumn(1).setPreferredWidth((int) (w * 0.20));
            m.getColumn(2).setPreferredWidth((int) (w * 0.10));
            m.getColumn(3).setPreferredWidth((int) (w * 0.10));
            m.getColumn(4).setPreferredWidth((int) (w * 0.10));
            m.getColumn(5).setPreferredWidth((int) (w * 0.10));
            m.getColumn(6).setPreferredWidth((int) (w * 0.20));
            m.getColumn(7).setPreferredWidth((int) (w * 0.15));
        };

        table.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeColumns.run();
            }
        });

        SwingUtilities.invokeLater(resizeColumns);
    }

    public void loadDataFromMongoDB() {
        model.setRowCount(0);
        List<Document> tasks = dbManager.getAllTasks();

        for (Document task : tasks) {
            Object[] row = {
                    task.getObjectId("_id").toString(),
                    task.getString("title"),
                    task.getString("category"),
                    task.getString("priority"),
                    task.getString("dueDate"),
                    task.getString("status"),
                    task.getString("description"),
                    ""
            };
            model.addRow(row);
        }
    }

    public void applyFilters(String priority, String dateFilterValue, String status) {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!priority.equals("All Priority")) {
            filters.add(RowFilter.regexFilter("^" + priority + "$", 3));
        }

        if (!status.equals("All Status")) {
            filters.add(RowFilter.regexFilter("^" + status + "$", 5));
        }

        if (!dateFilterValue.equals("All Dates")) {
            String targetDate = resolveDateFilter(dateFilterValue);
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<?, ?> entry) {
                    return entry.getStringValue(4).equals(targetDate);
                }
            });
        }

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private String resolveDateFilter(String dateFilterValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar cal = Calendar.getInstance();

        switch (dateFilterValue) {
            case "Yesterday":
                cal.add(Calendar.DATE, -1);
                break;
            case "Tomorrow":
                cal.add(Calendar.DATE, 1);
                break;
            case "Today":
            default:
                break;
        }

        return sdf.format(cal.getTime());
    }
}
