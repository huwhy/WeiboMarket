package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class MemberConfig implements Serializable {

    private int memberId;

    private String content;
    /**
     * 黑评数多少加入黑名单
     */
    private int badNumLimit;
    private boolean openBlack;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBadNumLimit() {
        return badNumLimit;
    }

    public void setBadNumLimit(int badNumLimit) {
        this.badNumLimit = badNumLimit;
    }

    public boolean isOpenBlack() {
        return openBlack;
    }

    public void setOpenBlack(boolean openBlack) {
        this.openBlack = openBlack;
    }
}
