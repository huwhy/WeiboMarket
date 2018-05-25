package cn.huwhy.weibo.robot.task;

import cn.huwhy.weibo.robot.model.WbAccount;
import org.openqa.selenium.WebDriver;

import java.io.Serializable;

public class TaskContext implements Serializable {

    private volatile boolean stop = false;

    private WbAccount account;

    private WebDriver driver;

    public TaskContext(WbAccount account, WebDriver driver) {
        this.account = account;
        this.driver = driver;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public WbAccount getAccount() {
        return account;
    }

    public void setAccount(WbAccount account) {
        this.account = account;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
