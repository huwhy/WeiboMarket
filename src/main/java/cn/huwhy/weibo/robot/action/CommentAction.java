package cn.huwhy.weibo.robot.action;

import cn.huwhy.common.util.CollectionUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.model.MyFans;
import cn.huwhy.weibo.robot.model.Task;
import cn.huwhy.weibo.robot.model.TaskStatus;
import cn.huwhy.weibo.robot.model.Word;
import cn.huwhy.weibo.robot.model.WordType;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.service.TaskService;
import cn.huwhy.weibo.robot.service.WordService;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

@Component
public class CommentAction {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Pattern timePattern = Pattern.compile("(\\d{1,2}月\\d{1,2}日\\s\\d{2}:\\d{2})|(\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{2}:\\d{2})");

    private WebDriver driver;
    private Member member;
    private Date minDate, maxDate;
    private volatile boolean running = false;
    private List<Word> words;
    private Set<Word> hitWords = new HashSet<>();
    private Map<Long, MyFans> fansMap = new HashMap<>();

    @Autowired
    private FansService fansService;
    @Autowired
    private WordService wordService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TaskService taskService;

    public CommentAction() {
    }

    public void init(WebDriver driver, Member member, Date minDate, Date maxDate) {
        this.driver = driver;
        this.member = member;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public void run(ActionCaller caller) {
        if (running) {
            return;
        }
        running = true;
        Task task = new Task();
        int total = 0, redNum = 0, delNum = 0;
        try {
            init();
            int totalPage = 0;
            String uri = "https://weibo.com/comment/inbox?topnav=1&wvr=6&f=1";
            this.driver.get(uri);
            ThreadUtil.sleep(1000);
            while (true) {
                List<WebElement> list = this.driver.findElements(By.cssSelector(".W_pages .page"));
                if (list.size() == 0) {
                    ThreadUtil.sleep(500);
                    continue;
                }
                for (WebElement el : list) {
                    if (isNumber(el.getText())) {
                        int val = Integer.parseInt(el.getText());
                        totalPage = val > totalPage ? val : totalPage;
                    }
                }
                break;
            }

            task.setMemberId(member.getId());
            task.setName("自动处理评论任务");
            task.setStartTime(new Date());
            task.setStatus(TaskStatus.ING);
            String summaryFormat = "处理%s条评论，发现删除%s条黑评论";
            task.setSummary(String.format(summaryFormat, 0, 0));
            taskService.save(task);
            String pageUri = "https://weibo.com/comment/inbox?page=";
            boolean found = false;
            int lPage = 1, rPage = totalPage;
            boolean low = false;
            for (int page = 1; page <= totalPage;) {
                if (page > 1) {
                    this.driver.get(pageUri + page);
                    ThreadUtil.sleep(500);
                    while (true) {
                        List<WebElement> list = this.driver.findElements(By.cssSelector(".W_pages .page"));
                        boolean ok = false;
                        for (WebElement el : list) {
                            if (el.getAttribute("class").contains("S_bg1")) {
                                ok = true;
                                break;
                            }
                        }
                        if (ok) {
                            break;
                        }
                        ThreadUtil.sleepSeconds(1);
                    }
                }
                //找到与日期对应的页
                List<WebElement> elements = getWebElements();
                if (!found) {
                    if (CollectionUtil.isNotEmpty(elements)) {
                        for (WebElement el : elements) {
                            String timeStr = el.findElement(By.cssSelector(".WB_detail .WB_from")).getText();
                            Pattern timePattern = Pattern.compile("(\\d{1,2}月\\d{1,2}日\\s\\d{2}:\\d{2})|(\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{2}:\\d{2})");
//                            Matcher matcher = timePattern.matcher(timeStr);
                            Matcher matcher = timePattern.matcher(timeStr);
                            int year = LocalDate.now().getYear();
                            if (matcher.find()) {
                                Date date;
                                try {
                                    date = DateUtils.parseDate(matcher.group(), new String[]{"yyyy年MM月dd日 HH:mm", "yyyy-MM-dd HH:mm"});
                                } catch (ParseException err) {
                                    date = DateUtils.parseDate(matcher.group(), new String[]{"MM月dd日 HH:mm", "M月dd日 HH:mm"});
                                    date = LocalDateTime.fromDateFields(date).plusYears(year - 1970).toDate();
                                }
                                if (date.before(maxDate)) {
                                    low = true;
                                    rPage = page;
                                    page = (lPage + rPage) / 2;
                                    if (rPage - lPage < 5) {
                                        page = lPage;
                                        found = true;
                                    }
                                    break;
                                }
                                if (date.after(maxDate)) {
                                    low = false;
                                    lPage = page;
                                    page = (lPage + rPage) / 2;
                                    if (rPage - lPage < 5) {
                                        found = true;
                                    }
                                    break;
                                }
                            }
                        }
                        if (found) {
                            continue;
                        }
                    } else {
                        if (low) {
                            rPage = page;
                            page = (lPage + rPage) / 2;
                            continue;
                        } else {
                            lPage = page;
                            page = (lPage + rPage) / 2;
                            continue;
                        }
                    }
                }
                if (found) {
                    List<MyFans> myFansList = new ArrayList<>();
                    elements = getWebElements();
                    if (CollectionUtil.isNotEmpty(elements)) {
                        for (WebElement el : elements) {
                            String timeStr = el.findElement(By.cssSelector(".WB_detail .WB_from")).getText();
                            Matcher matcher = timePattern.matcher(timeStr);
                            int year = LocalDate.now().getYear();
                            if (matcher.find()) {
                                Date date;
                                try {
                                    date = DateUtils.parseDate(matcher.group(), new String[]{"yyyy年MM月dd日 HH:mm"});
                                } catch (ParseException err) {
                                    date = DateUtils.parseDate(matcher.group(), new String[]{"MM月dd日 HH:mm", "M月dd日 HH:mm"});
                                    date = LocalDateTime.fromDateFields(date).plusYears(year - 1970).toDate();
                                }
                                if (date.before(minDate)) {
                                    totalPage = page;
                                    break;
                                }
                                if (date.after(maxDate)) {
                                    page += 1;
                                    break;
                                }
                            }

                            String href = el.findElement(By.cssSelector(".face a")).getAttribute("href");
                            String nick = el.findElement(By.cssSelector(".WB_detail .WB_info a")).getText();
                            String text = el.findElement(By.cssSelector(".WB_detail .WB_text")).getText();
                            total++;
                            WebElement imgEl = el.findElement(By.cssSelector(".face a img"));
                            String img = imgEl.getAttribute("src");
                            String usercard = imgEl.getAttribute("usercard").replace("id=", "");
                            Long myFansId = Long.valueOf(usercard);
                            MyFans fans = fansMap.get(myFansId);
                            if (fans == null) {
                                fans = new MyFans();
                                fans.setId(myFansId);
                                fans.setType(WordType.MASS);
                                fansMap.put(myFansId, fans);
                            }
                            fans.setNick(nick);
                            fans.setHome(href);
                            fans.setHeadImg(img);
                            fans.setMemberId(this.member.getId());
                            myFansList.add(fans);
                            if (fans.getType().equals(WordType.BLACK)) {
                                delNum++;
                                deleteComment(el, this.member.getConfig().isOpenBlack());
                            } else {
                                for (Word word : words) {
                                    if (text.contains(word.getWord())) {
                                        word.setHitNum(word.getHitNum() + 1);
                                        this.hitWords.add(word);
                                        fans.setType(word.getType());
                                        if (word.getType() == WordType.BLACK) {
                                            deleteComment(el, member.getConfig().isOpenBlack() && member.getConfig().getBadNumLimit() <= fans.getBadNum());
                                            delNum += 1;
                                            break;
                                        } else if (word.getType() == WordType.IRON) {
                                            redNum += 1;
                                            break;
                                        }
                                    }
                                }
                            }

                        }

                        task.setSummary(String.format(summaryFormat, total, delNum));
                        taskService.save(task);
                        caller.call();
                        fansService.save(myFansList);
                        page++;
                    } else if (page <= totalPage) {
                        page++;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("", e);
        } finally {
            task.setStatus(TaskStatus.FINISHED);
            task.setEndTime(new Date());
            taskService.save(task);
            if (!fansMap.isEmpty()) {
                fansService.save(fansMap.values());
                if (!hitWords.isEmpty()) {
                    wordService.saves(hitWords);
                }
            }
            member.setCommentNum(member.getCommentNum() + total);
            member.setBlackCommentNum(member.getBlackCommentNum() + delNum);
            member.setRedCommentNum(member.getRedCommentNum() + redNum);
            memberService.save(member);
            caller.call();
            running = false;
        }
    }

    private List<WebElement> getWebElements() {
        do {
            try {
                return driver.findElements(By.cssSelector(".WB_feed_comment .WB_cardwrap[node-type=feed_commentList_comment]"));
            } catch (Throwable e) {
                ThreadUtil.sleep(600);
            }
        } while (true);
    }

    private void deleteComment(WebElement element, boolean addList) {
        ActionUtil.click(element, ".screen_box");
        ActionUtil.click(element, ".layer_menu_list a[action-type=delComment]");
        ThreadUtil.sleepSeconds(1);
        if (addList) {
            ActionUtil.click(driver, ".W_layer input[name=block_user]");
        }
        ActionUtil.click(driver, ".W_layer .W_layer_btn a[action-type=ok]");
        ThreadUtil.sleepSeconds(1);
    }

    private void init() {
        logger.info("comment action init start");
        this.words = wordService.listMyWords(this.member.getId());
        this.fansMap.clear();
    }

}
