package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.weibo.robot.model.WbAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WbAccountDao extends BaseDao<WbAccount, Integer> {

    List<WbAccount> getByMemberId(@Param("memberId") int memberId,
                                  @Param("num") int num);

    WbAccount getFirst();
}
