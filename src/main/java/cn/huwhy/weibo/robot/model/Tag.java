package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class Tag implements Serializable {

    private long id;

    private String word;

    private int hitNum;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getHitNum() {
        return hitNum;
    }

    public void setHitNum(int hitNum) {
        this.hitNum = hitNum;
    }
}
