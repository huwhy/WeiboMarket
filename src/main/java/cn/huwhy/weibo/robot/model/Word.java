package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class Word implements Serializable {
    private long id;
    private int memberId;
    private String word;
    private WordType type;
    private int hitNum;

    public Word() {
    }

    public Word(long id, String word, WordType type, int hitNum) {
        this.id = id;
        this.word = word;
        this.type = type;
        this.hitNum = hitNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    public int getHitNum() {
        return hitNum;
    }

    public void setHitNum(int hitNum) {
        this.hitNum = hitNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return id == word.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
