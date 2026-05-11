package todo.com.ui;

import javax.swing.ImageIcon;
import java.awt.Image;

public final class AppIcons {
    private static final String APP_ICON_RESOURCE = "/todo-app-logo.png";
    private static Image appIconImage;

    private AppIcons() {
    }

    public static Image getAppIconImage() {
        if (appIconImage == null) {
            java.net.URL iconUrl = AppIcons.class.getResource(APP_ICON_RESOURCE);
            if (iconUrl != null) {
                appIconImage = new ImageIcon(iconUrl).getImage();
            }
        }
        return appIconImage;
    }
}
