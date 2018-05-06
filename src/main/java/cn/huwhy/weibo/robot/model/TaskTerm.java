package cn.huwhy.weibo.robot.model;

import cn.huwhy.interfaces.Term;

public class TaskTerm extends Term {
    private int memberId;
    private TaskStatus status;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
