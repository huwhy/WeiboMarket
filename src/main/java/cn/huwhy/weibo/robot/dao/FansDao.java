package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.weibo.robot.model.Fans;
import cn.huwhy.weibo.robot.model.FansTerm;
import cn.huwhy.weibo.robot.model.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface FansDao extends BaseDao<Fans, Fans> {

    void savesByMemberId(@Param("memberId") int memberId, @Param("ids")Collection<Long> ids);

    List<String> findFansPaging(FansTerm term);
}
