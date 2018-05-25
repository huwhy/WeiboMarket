package cn.huwhy.weibo.robot.model.common;

import cn.huwhy.interfaces.EnumValue;

public enum SearchType implements EnumValue<String> {
    综合,
    找人;

    @Override
    public String getValue() {
        return name();
    }
}
