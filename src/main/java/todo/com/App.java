package todo.com;

import com.formdev.flatlaf.FlatLightLaf;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import todo.com.database.MongoDBManager;
import todo.com.ui.MainWindow;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        MongoDBManager dbManager = MongoDBManager.getInstance();

        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            IconFontSwing.register(FontAwesome.getIconFont());

            Runtime.getRuntime().addShutdownHook(new Thread(dbManager::closeConnection));

            MainWindow frame = new MainWindow(dbManager);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
