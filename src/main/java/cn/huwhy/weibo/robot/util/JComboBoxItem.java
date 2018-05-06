package cn.huwhy.weibo.robot.util;

/**
 * 说明:自定义下拉菜单元素的工具类
 */
public class JComboBoxItem<T> {
    private T data;

    public JComboBoxItem(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return data.toString();
    }
}
