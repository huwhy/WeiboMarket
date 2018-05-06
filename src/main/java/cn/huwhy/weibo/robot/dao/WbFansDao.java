package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.interfaces.Term;
import cn.huwhy.weibo.robot.model.MyFans;
import cn.huwhy.weibo.robot.model.WbFans;

import java.util.List;

public interface WbFansDao extends BaseDao<WbFans, WbFans> {
    List<MyFans> findFansPaging(Term term);
}
