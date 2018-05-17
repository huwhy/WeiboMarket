package cn.huwhy.weibo.robot.dao;

import cn.huwhy.ibatis.BaseDao;
import cn.huwhy.weibo.robot.model.Tag;
import cn.huwhy.weibo.robot.model.Word;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagDao extends BaseDao<Tag, Long> {

    int plusHitNum(@Param("id") long id, @Param("num") int num);

    Tag getByWord(@Param("word") String word);
}
