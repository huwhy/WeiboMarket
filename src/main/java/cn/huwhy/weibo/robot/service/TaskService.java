package cn.huwhy.weibo.robot.service;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.interfaces.Term;
import cn.huwhy.weibo.robot.dao.TaskDao;
import cn.huwhy.weibo.robot.model.Task;
import cn.huwhy.weibo.robot.model.TaskTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    private TaskDao taskDao;

    public void save(Task task) {
        taskDao.save(task);
    }

    public Task get(long id) {
        return taskDao.get(id);
    }

    public Paging<Task> findTasks(TaskTerm term) {
        term.addSort("id", Term.Sort.DESC);
        List<Task> list = taskDao.findPaging(term);
        return new Paging<>(term, list);
    }

    public void submit(Runnable runnable, long delaySeconds) {
        executorService.scheduleWithFixedDelay(runnable, 0, delaySeconds, TimeUnit.SECONDS);
    }

    public void submit(Runnable runnable) {
        executorService.schedule(runnable, 1, TimeUnit.SECONDS);
    }

}
