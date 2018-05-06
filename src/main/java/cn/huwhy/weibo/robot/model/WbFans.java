package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class WbFans implements Serializable {
    private int id;
    private long wbId;
    private WordType type;
    private int goodNum;
    private int badNum;

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

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getBadNum() {
        return badNum;
    }

    public void setBadNum(int badNum) {
        this.badNum = badNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WbFans wbFans = (WbFans) o;

        if (id != wbFans.id) return false;
        return wbId == wbFans.wbId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (wbId ^ (wbId >>> 32));
        return result;
    }
}
