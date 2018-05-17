package cn.huwhy.weibo.robot.model;

import java.io.Serializable;

public class WbMember implements Serializable {

    private long id;
    private String nick;
    private String home;
    private String headImg;
    //性别 female  male
    private String sex;
    //地区  省市
    private String addr;
    //微博标签
    private String card;
    private boolean bigV;
    private int fansNum;
    private String vTagIds;
    private String tagIds;
    private int goodNum;
    private int badNum;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public boolean isBigV() {
        return bigV;
    }

    public void setBigV(boolean bigV) {
        this.bigV = bigV;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public String getvTagIds() {
        return vTagIds;
    }

    public void setvTagIds(String vTagIds) {
        this.vTagIds = vTagIds;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
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

        WbMember wbMember = (WbMember) o;

        return id == wbMember.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
