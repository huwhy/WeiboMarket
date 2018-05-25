package cn.huwhy.weibo.robot.model;

import cn.huwhy.weibo.robot.model.common.SearchType;
import cn.huwhy.weibo.robot.ui.MyCheckBox;

import java.io.Serializable;

public class SearchResult implements Serializable {
    private long id;
    private int memberId;
    private SearchType type;
    private String title;
    private String content;
    private String url;
    private MyCheckBox cb = new MyCheckBox();

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

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MyCheckBox getCb() {
        return cb;
    }

    public void setCb(MyCheckBox cb) {
        this.cb = cb;
    }
}
