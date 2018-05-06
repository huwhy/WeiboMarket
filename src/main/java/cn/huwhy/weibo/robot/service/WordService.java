package cn.huwhy.weibo.robot.service;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.interfaces.Term;
import cn.huwhy.weibo.robot.dao.WordDao;
import cn.huwhy.weibo.robot.model.Word;
import cn.huwhy.weibo.robot.model.WordTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class WordService {
    @Autowired
    private WordDao wordDao;

    public Word get(long id) {
        return wordDao.get(id);
    }

    public void save(Word word) {
        wordDao.save(word);
    }

    public Paging<Word> findWords(WordTerm term) {
        term.addSort("id", Term.Sort.DESC);
        List<Word> list = wordDao.findPaging(term);
        return new Paging<>(term, list);
    }

    public List<Word> listMyWords(int memberId) {
        return wordDao.listMyWords(memberId);
    }

    public void delete(long id) {
        wordDao.delete(id);
    }

    public void saves(Set<Word> hitWords) {
        this.wordDao.saves(hitWords);
    }
}
