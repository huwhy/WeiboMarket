package cn.huwhy.weibo.robot.model;

import cn.huwhy.interfaces.Term;

public class WordTerm extends Term {
    private WordType type;
    private int memberId;

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
