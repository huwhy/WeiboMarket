package cn.huwhy.weibo.robot.service;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.dao.FansDao;
import cn.huwhy.weibo.robot.dao.MemberDao;
import cn.huwhy.weibo.robot.dao.TagDao;
import cn.huwhy.weibo.robot.dao.WbMemberDao;
import cn.huwhy.weibo.robot.model.FansTerm;
import cn.huwhy.weibo.robot.model.Tag;
import cn.huwhy.weibo.robot.model.WbMember;
import com.google.common.collect.Collections2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FansService {
    @Autowired
    private FansDao fansDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private WbMemberDao wbMemberDao;


    public void saveTag(Tag tag) {
        Tag dbTag = tagDao.getByWord(tag.getWord());
        if (dbTag == null) {
            tagDao.save(tag);
        } else {
            tag.setId(dbTag.getId());
            tag.setHitNum(dbTag.getHitNum());
        }
    }

    public void saveWbMembers(int memberId, List<WbMember> wbMembers) {
        if (wbMembers.isEmpty()) return;
        wbMemberDao.saves(wbMembers);
        Collection<Long> ids = Collections2.transform(wbMembers, WbMember::getId);
        fansDao.savesByMemberId(memberId, ids);
    }

    public Paging<String> findFansHomeList(FansTerm term) {
        List<String> homeList = fansDao.findFansPaging(term);
        return new Paging<>(term, homeList);
    }
}
