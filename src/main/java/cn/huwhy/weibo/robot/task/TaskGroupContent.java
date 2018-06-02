package cn.huwhy.weibo.robot.task;

import cn.huwhy.weibo.robot.model.WbAccount;
import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.List;

public class TaskGroupContent implements Serializable {

    private WebDriver driver;

    private List<WbAccount> accounts;

    private int curAccountIndex;

    public TaskGroupContent(List<WbAccount> accounts) {
        this.accounts = accounts;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public List<WbAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<WbAccount> accounts) {
        this.accounts = accounts;
    }

    public int getCurAccountIndex() {
        return curAccountIndex;
    }

    public void setCurAccountIndex(int curAccountIndex) {
        this.curAccountIndex = curAccountIndex;
    }
}
