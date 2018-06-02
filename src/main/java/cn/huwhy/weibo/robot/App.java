package cn.huwhy.weibo.robot;

import cn.huwhy.weibo.robot.util.ResourcesUtil;
import cn.huwhy.weibo.robot.util.SystemType;
import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class App {
    static {
        try {
            if (ResourcesUtil.SYSTEM_TYPE == SystemType.MAC) {
                Image icon_image;
                icon_image = ImageIO.read(App.class.getClassLoader().getResourceAsStream("logo.jpeg"));
                Application app = Application.getApplication();
                app.setDockIconImage(icon_image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            AppWindow.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
