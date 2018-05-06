package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.weibo.robot.model.Member;
import org.apache.ibatis.annotations.Param;

public interface MemberDao extends BaseDao<Member, Integer> {

    Member getByName(@Param("name") String name);
}
