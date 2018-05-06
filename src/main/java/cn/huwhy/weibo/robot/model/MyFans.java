package cn.huwhy.weibo.robot.model;

public class MyFans extends WbMember {
    private int memberId;
    private WordType type;
    private int goodNum;
    private int badNum;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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
        MyFans myFans = (MyFans) o;

        return memberId == myFans.memberId && getId() == myFans.getId();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + memberId;
        return result;
    }
}
