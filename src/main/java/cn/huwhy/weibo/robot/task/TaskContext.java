package cn.huwhy.weibo.robot.task;

import cn.huwhy.weibo.robot.model.SearchResult;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.WbMember;
import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskContext implements Serializable {

    private volatile boolean finished = false;
    private volatile boolean next = true;

    private WbAccount account;

    private WebDriver driver;
    private Set<String> set = new HashSet<>();
    private int counter;
    private int cnter;
    private int limitNum;
    private int page = 1;
    private SearchResult result;
    private List<WbMember> wbMembers = new ArrayList<>();

    public TaskContext(WbAccount account, WebDriver driver, int limitNum) {
        this.account = account;
        this.driver = driver;
        this.limitNum = limitNum;
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

    public Set<String> getSet() {
        return set;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void incrCounter() {
        this.counter++;
        this.cnter++;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public List<WbMember> getWbMembers() {
        return wbMembers;
    }

    public void addWbMembers(WbMember wbMember) {
        this.wbMembers.add(wbMember);
    }

    public void clearWbMembers() {
        this.wbMembers.clear();
    }

    public int getCounter() {
        return counter;
    }

    public int getCnter() {
        return cnter;
    }

    public int getPage() {
        return page;
    }

    public void incrPage() {
        this.page++;
        this.cnter = 0;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public SearchResult getResult() {
        return result;
    }

    public void setResult(SearchResult result) {
        this.result = result;
    }
}
