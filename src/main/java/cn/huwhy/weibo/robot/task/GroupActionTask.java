package cn.huwhy.weibo.robot.task;

import java.io.Serializable;

public abstract class GroupActionTask implements Serializable {

    private TaskGroupContent content;

    public GroupActionTask(TaskGroupContent content) {
        this.content = content;
    }

    public abstract void run();

}
