package cn.huwhy.weibo.robot.model;

import java.io.Serializable;
import java.util.Date;

public class Member implements Serializable {
    private int id;
    private String name;
    private String password;
    private String wbName;
    private String wbPassword;
    private int commentNum;
    private int redCommentNum;
    private int blackCommentNum;
    private long lastCommentId;
    private int wbNum;
    private Date endTime;
    private Date creatd;

    public Member() {
    }

    public Member(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWbName() {
        return wbName;
    }

    public void setWbName(String wbName) {
        this.wbName = wbName;
    }

    public String getWbPassword() {
        return wbPassword;
    }

    public void setWbPassword(String wbPassword) {
        this.wbPassword = wbPassword;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getRedCommentNum() {
        return redCommentNum;
    }

    public void setRedCommentNum(int redCommentNum) {
        this.redCommentNum = redCommentNum;
    }

    public int getBlackCommentNum() {
        return blackCommentNum;
    }

    public void setBlackCommentNum(int blackCommentNum) {
        this.blackCommentNum = blackCommentNum;
    }

    public long getLastCommentId() {
        return lastCommentId;
    }

    public void setLastCommentId(long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }

    public int getWbNum() {
        return wbNum;
    }

    public void setWbNum(int wbNum) {
        this.wbNum = wbNum;
    }

    private MemberConfig config = new MemberConfig();

    public MemberConfig getConfig() {
        return config;
    }

    public void setConfig(MemberConfig config) {
        this.config = config;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreatd() {
        return creatd;
    }

    public void setCreatd(Date creatd) {
        this.creatd = creatd;
    }
}
