package cn.huwhy.weibo.robot.model;

import cn.huwhy.weibo.robot.ui.MyCheckBox;

import java.io.Serializable;

public class WbAccount implements Serializable {
    private int id;
    private int memberId;

    private String username;

    private String password;

    private MyCheckBox cb = new MyCheckBox();

    public WbAccount() {
    }

    public WbAccount(int memberId, String username, String password) {
        this.memberId = memberId;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MyCheckBox getCb() {
        return cb;
    }

    public void setCb(MyCheckBox cb) {
        this.cb = cb;
    }
}
