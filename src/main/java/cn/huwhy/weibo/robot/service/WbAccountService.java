package cn.huwhy.weibo.robot.service;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.dao.WbAccountDao;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.common.WbAccountTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WbAccountService {

    @Autowired
    private WbAccountDao wbAccountDao;

    public void save(WbAccount account) {
        wbAccountDao.save(account);
    }

    public WbAccount get(int id) {
        return wbAccountDao.get(id);
    }

    public List<WbAccount> getByMemberId(int memberId, int num) {
        return wbAccountDao.getByMemberId(memberId, num);
    }

    public Paging<WbAccount> findPaging(WbAccountTerm term) {
        if (StringUtil.isNotEmpty(term.getUsername())) {
            term.setUsername("%" + term.getUsername() + "%");
        }
        List<WbAccount> list = wbAccountDao.findPaging(term);
        return new Paging<>(term, list);
    }

    public void delById(int id) {
        wbAccountDao.delete(id);
    }

    public void delByIds(List<Integer> ids) {
        wbAccountDao.deletes(ids);
    }
}
