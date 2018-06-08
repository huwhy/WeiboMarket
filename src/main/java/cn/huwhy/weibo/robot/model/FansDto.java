package cn.huwhy.weibo.robot.model;

import cn.huwhy.weibo.robot.ui.MyCheckBox;

import java.io.Serializable;

public class FansDto extends WbMember implements Serializable {
    private long wbId;
    private MyCheckBox cb = new MyCheckBox();

    public long getWbId() {
        return wbId;
    }

    public void setWbId(long wbId) {
        this.wbId = wbId;
    }

    public MyCheckBox getCb() {
        return cb;
    }

    public void setCb(MyCheckBox cb) {
        this.cb = cb;
    }
}
