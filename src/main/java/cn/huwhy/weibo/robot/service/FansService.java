package cn.huwhy.weibo.robot.service;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.dao.WbFansDao;
import cn.huwhy.weibo.robot.dao.WbMemberDao;
import cn.huwhy.weibo.robot.model.MyFans;
import cn.huwhy.weibo.robot.model.MyFansTerm;
import cn.huwhy.weibo.robot.model.WbFans;
import cn.huwhy.weibo.robot.model.WbMember;
import cn.huwhy.common.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cn.huwhy.common.util.BeanCopyUtils.copyProperties;

@Service
public class FansService {
    @Autowired
    private WbMemberDao wbMemberDao;
    @Autowired
    private WbFansDao wbFansDao;

    public Paging<MyFans> findMyFans(MyFansTerm term) {
        List<MyFans> list = wbFansDao.findFansPaging(term);
        return new Paging<>(term, list);
    }

    @Transactional
    public void save(MyFans myFans) {
        WbFans fans = new WbFans();
        fans.setId(myFans.getMemberId());
        fans.setType(myFans.getType());
        fans.setWbId(myFans.getId());
        wbFansDao.save(fans);
    }

    @Transactional
    public void save(Collection<MyFans> myFansList) {
        if (myFansList.isEmpty()) return;
        List<WbMember> wbMembers = new ArrayList<>();
        List<WbFans> wbFansList = new ArrayList<>();
        for (MyFans myFans : myFansList) {
            WbMember wbMember = copyProperties(myFans, WbMember.class);
            wbMembers.add(wbMember);
            WbFans fans = new WbFans();
            fans.setId(myFans.getMemberId());
            fans.setType(myFans.getType());
            fans.setWbId(myFans.getId());
            fans.setGoodNum(myFans.getGoodNum());
            fans.setBadNum(myFans.getBadNum());
            wbFansList.add(fans);
        }
        wbMemberDao.saves(wbMembers);
        wbFansDao.saves(wbFansList);
    }
}
