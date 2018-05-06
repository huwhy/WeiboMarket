package cn.huwhy.weibo.robot.controller;

public abstract class BaseController {
    private BaseController parent;

    public BaseController getParent() {
        return parent;
    }

    public void setParent(BaseController parent) {
        this.parent = parent;
    }

    public void refresh() {
    }

    public void init() {

    }

}
