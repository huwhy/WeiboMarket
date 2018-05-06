package cn.huwhy.weibo.robot;

import cn.huwhy.weibo.robot.controller.BaseController;
import cn.huwhy.weibo.robot.model.Member;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;

public final class AppContext implements Serializable {
    private static final AppContext ctx = new AppContext();

    private AppContext() {
    }

    private Member member;
    private Stage mainState, modelState;
    private volatile boolean isAutoTask;

    public static void setMember(Member member) {
        ctx.member = member;
    }

    public static void setMainState(Stage mainState) {
        ctx.mainState = mainState;
    }

    public static Member getMember() {
        return ctx.member;
    }

    public static int getMemberId() {
        return ctx.member == null ? 0 : ctx.member.getId();
    }

    public static <T> T showModel(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(AppContext.class.getClassLoader().getResource(fxml));
        Parent target = loader.load();
        Scene scene = new Scene(target); //创建场景；
        ctx.modelState = new Stage();//创建舞台；
        ctx.modelState.initModality(Modality.APPLICATION_MODAL);
        ctx.modelState.initOwner(ctx.mainState);
        ctx.modelState.setScene(scene); //将场景载入舞台；
        ctx.modelState.sizeToScene();
        ctx.modelState.show();
        return loader.getController();
    }

    public static void closeModel() {
        if (ctx.modelState != null) {
            ctx.modelState.hide();
            ctx.modelState = null;
        }
    }

    public static Parent loadFxml(String fxml) {
        FXMLLoader loader = new FXMLLoader(AppContext.class.getClassLoader().getResource(fxml));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showLogin() {
        try {
            replaceSceneContent("login.fxml", 800, 600);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void showMain(Member member) {
        try {
            AppContext.setMember(member);
            BaseController main = replaceSceneContent("main.fxml", 1024, 700);
            main.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BaseController replaceSceneContent(String fxml, double width, double height) throws Exception {
        FXMLLoader loader = new FXMLLoader(AppContext.class.getClassLoader().getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        ctx.mainState.setScene(scene);
        ctx.mainState.sizeToScene();
        return (BaseController) loader.getController();
    }

    public static void setAutoTask(boolean autoTask) {
        ctx.isAutoTask = autoTask;
    }

    public static boolean getAutoTask() {
        return ctx.isAutoTask;
    }
}
