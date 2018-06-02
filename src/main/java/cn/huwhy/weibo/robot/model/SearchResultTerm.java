package cn.huwhy.weibo.robot.model;

import cn.huwhy.interfaces.Term;

import java.util.Date;

public class SearchResultTerm extends Term {

    private Date createdFrom;

    private Date createdTo;

    private int memberId;

    public Date getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Date createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Date getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(Date createdTo) {
        this.createdTo = createdTo;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
