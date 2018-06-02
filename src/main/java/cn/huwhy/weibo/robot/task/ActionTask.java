package cn.huwhy.weibo.robot.task;

import java.io.Serializable;

public abstract class ActionTask implements Serializable {

    protected TaskContext context;

    public ActionTask(TaskContext context) {
        this.context = context;
    }

    public abstract void run();

    public TaskContext getContext() {
        return context;
    }
}
