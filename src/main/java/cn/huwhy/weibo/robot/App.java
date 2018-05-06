package cn.huwhy.weibo.robot;

import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class App {
    static {
        try {
            Image icon_image;
            icon_image = ImageIO.read(App.class.getClassLoader().getResourceAsStream("logo.jpeg"));
            Application app = Application.getApplication();
            app.setDockIconImage(icon_image);

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
