package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class Fans implements Serializable {
    private int id;
    private long wbId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getWbId() {
        return wbId;
    }

    public void setWbId(long wbId) {
        this.wbId = wbId;
    }
}
