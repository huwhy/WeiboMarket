package cn.huwhy.weibo.robot.model.common;

import cn.huwhy.interfaces.Term;

public class WbAccountTerm extends Term {

    private String username;

    private int memberId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
