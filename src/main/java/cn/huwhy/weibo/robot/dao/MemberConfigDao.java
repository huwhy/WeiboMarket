package cn.huwhy.weibo.robot.dao;

import cn.huwhy.weibo.robot.model.MemberConfig;

public interface MemberConfigDao {

    void save(MemberConfig po);

    String get(int id);

}
