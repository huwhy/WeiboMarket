package cn.huwhy.weibo.robot.model;

import cn.huwhy.interfaces.EnumValue;

public enum TaskStatus implements EnumValue<Integer> {
    ING("进行中", 1),
    FINISHED("结束", 2);

    TaskStatus(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private Integer value;

    @Override
    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
