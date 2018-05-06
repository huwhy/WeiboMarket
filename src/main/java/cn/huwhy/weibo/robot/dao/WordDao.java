package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.weibo.robot.model.Word;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WordDao extends BaseDao<Word, Long> {

    List<Word> listMyWords(@Param("memberId") int memberId);

    int plusHitNum(@Param("id") long id, @Param("num") int num);

}
