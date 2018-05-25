package cn.huwhy.weibo.robot.task;

import java.io.Serializable;

public abstract class ActionTask implements Runnable, Serializable {

    protected TaskContext context;

    public ActionTask(TaskContext context) {
        this.context = context;
    }

    public abstract void run();

}
