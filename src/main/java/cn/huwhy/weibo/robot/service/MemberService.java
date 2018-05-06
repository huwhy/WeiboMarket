package cn.huwhy.weibo.robot.service;

import cn.huwhy.common.json.JsonUtil;
import cn.huwhy.common.util.StringUtil;
import cn.huwhy.weibo.robot.dao.MemberConfigDao;
import cn.huwhy.weibo.robot.dao.MemberDao;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.model.MemberConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberConfigDao memberConfigDao;

    public Member get(int id) {
        return memberDao.get(id);
    }

    public Member getByName(String name) {
        return memberDao.getByName(name);
    }

    @Transactional
    public void save(Member member) {
        this.memberDao.save(member);
        if (member.getConfig() != null) {
            saveConfig(member.getConfig());
        }
    }

    public void saveConfig(MemberConfig config) {
        config.setContent("");
        config.setContent(JsonUtil.toJson(config));
        memberConfigDao.save(config);
    }

    public MemberConfig getConfig(int memberId) {
        String content = memberConfigDao.get(memberId);
        MemberConfig config;
        if (StringUtil.isNotEmpty(content)) {
            config = JsonUtil.toObject(content, MemberConfig.class);
        } else {
            config = new MemberConfig();
            config.setMemberId(memberId);
            config.setBadNumLimit(0);
        }
        return config;
    }
}
