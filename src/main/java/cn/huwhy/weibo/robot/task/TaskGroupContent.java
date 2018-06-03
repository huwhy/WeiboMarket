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

public class TaskGroupContent implements Serializable {

    private WebDriver driver;
    private SearchResult result;
    private List<WbAccount> accounts;
    private int nextAccountIndex = 0;
    private boolean finished;
    private boolean nextAccount = true;
    private boolean nextResult = true;
    private Set<String> set = new HashSet<>();
    //总计数
    private int totalCnt;
    //本个result计数
    private int resutlCounter;
    //帐号发送计数
    private int accontCounter;
    private int limitNum = 10;
    private int page = 1;
    private String msg;
    private List<WbMember> wbMembers = new ArrayList<>();

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

    public WbAccount nextAccount() {
        return nextAccountIndex < accounts.size() ? accounts.get(nextAccountIndex++) : null;
    }

    public SearchResult getResult() {
        return result;
    }

    public void setResult(SearchResult result) {
        this.result = result;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isNextAccount() {
        return nextAccount;
    }

    public void setNextAccount(boolean nextAccount) {
        this.nextAccount = nextAccount;
        if (nextAccount) {
            accontCounter = 0;
        }
    }

    public boolean isNextResult() {
        return nextResult;
    }

    public void setNextResult(boolean nextResult) {
        this.nextResult = nextResult;
        if (nextResult) {
            this.resutlCounter = 0;
        }
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

    public Set<String> getSet() {
        return set;
    }

    public void incrCounter() {
        this.totalCnt++;
        this.accontCounter++;
        this.resutlCounter++;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResutlCounter() {
        return resutlCounter;
    }

    public int getAccontCounter() {
        return accontCounter;
    }

    public int getPage() {
        return page;
    }

    public void incrPage() {
        this.page++;
        this.resutlCounter = 0;
    }

}
