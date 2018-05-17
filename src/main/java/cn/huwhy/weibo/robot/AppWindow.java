package cn.huwhy.weibo.robot;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.InputStream;

public class AppWindow extends Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("战狼微博娱情监测系统V1.0!");
        AppContext.setMainState(primaryStage);
        AppContext.showLogin();
        InputStream iconStream = AppWindow.class.getClassLoader().getResourceAsStream("logo.jpeg");
        primaryStage.getIcons().add(new Image(iconStream));
        primaryStage.show();

    }


}
