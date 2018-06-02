package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.CollectionUtil;
import cn.huwhy.common.util.DateUtil;
import cn.huwhy.common.util.RandomUtil;
import cn.huwhy.common.util.StringUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.action.ActionUtil;
import cn.huwhy.weibo.robot.model.SearchResult;
import cn.huwhy.weibo.robot.model.Tag;
import cn.huwhy.weibo.robot.model.Task;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.WbMember;
import cn.huwhy.weibo.robot.model.common.SearchType;
import cn.huwhy.weibo.robot.service.ChromeBrowserService;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.service.WbAccountService;
import cn.huwhy.weibo.robot.task.ActionTask;
import cn.huwhy.weibo.robot.task.GroupActionTask;
import cn.huwhy.weibo.robot.task.TaskContext;
import cn.huwhy.weibo.robot.task.TaskGroupContent;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import com.google.common.collect.Collections2;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;

import static java.net.URLEncoder.encode;

public class MarketController extends BaseController implements Initializable {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @FXML
    private TextArea txtContent;
    @FXML
    private Label lbTip, lbResult;
    @FXML
    private ListView<String> listView;
    private List<WbAccount> wbAccounts;
    private ArrayBlockingQueue<SearchResult> queue = new ArrayBlockingQueue<>(50);
    private ChromeBrowserService chromeBrowserService;
    private FansService fansService;
    private WbAccountService wbAccountService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chromeBrowserService = SpringContentUtil.getBean(ChromeBrowserService.class);
        fansService = SpringContentUtil.getBean(FansService.class);
        wbAccountService = SpringContentUtil.getBean(WbAccountService.class);
        TableColumn<Task, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Task, String> colName = new TableColumn<>("任务名称");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Task, String> colStartTime = new TableColumn<>("开始时间");
        colStartTime.setCellValueFactory(new PropertyValueFactory<Task, String>("startTime") {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Task, String> param) {
                return new ReadOnlyObjectWrapper<>(DateUtil.toStringTime(param.getValue().getStartTime()));
            }
        });
        TableColumn<Task, String> colStatus = new TableColumn<>("状态");
        colStatus.setCellValueFactory(new PropertyValueFactory<Task, String>("status") {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Task, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getStatus().getName());
            }
        });
        TableColumn<Task, String> colSummary = new TableColumn<>("描述");
        colSummary.setCellValueFactory(new PropertyValueFactory<>("summary"));
        TableColumn<Task, String> colEndTime = new TableColumn<>("结束时间");
        colEndTime.setCellValueFactory(new PropertyValueFactory<Task, String>("startTime") {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Task, String> param) {
                return new ReadOnlyObjectWrapper<>(DateUtil.toStringTime(param.getValue().getStartTime()));
            }
        });
        AppContext.setMarketController(this);
    }

    public void btnExecSms() {
        if (StringUtil.isEmpty(txtContent.getText())) {
            lbTip.setText("请输入私信内容");
            return;
        }
        wbAccounts = wbAccountService.getByMemberId(AppContext.getMemberId(), 100);
        int wbNum = AppContext.getMember().getWbNum();
        int groups = wbAccounts.size() / wbNum + 1;
        List<ActionTask> taskList = new ArrayList<>(groups);
        List<GroupActionTask> groupActionTasks = new ArrayList<>(groups);
        int aIndex = 0;
        for (int i = 0; i < groups; i++) {
            List<WbAccount> accounts = new ArrayList<>(wbNum);
            for (int j =  wbNum * i; j < wbAccounts.size() && j < wbNum * (i + 1); j++) {
                accounts.add(wbAccounts.get(j));
            }
            GroupActionTask task = new GroupActionTask(new TaskGroupContent(accounts)) {
                @Override
                public void run() {

                }
            };
            groupActionTasks.add(task);
        }
        for (WbAccount account : wbAccounts) {
            WebDriver driver = AppContext.getDriver(account.getUsername());
            if (driver == null) {
                driver = MarketController.this.chromeBrowserService.getDriver(account);
                AppContext.putDriver(account.getUsername(), driver);
            }
            ActionTask task = new ActionTask(new TaskContext(account, driver, 10)) {
                @Override
                public void run() {
                    SearchResult result;
                    if (this.context.isNext()) {
                        result = queue.poll();
                        this.context.setResult(result);
                        if (this.context.getWbMembers().size() > 0) {
                            fansService.saveWbMembers(AppContext.getMemberId(), this.context.getWbMembers());
                            this.context.clearWbMembers();
                        }
                    } else {
                        result = context.getResult();
                    }
                    if (result != null) {
                        if (this.context.isNext()) {
                            this.context.getDriver().get(result.getUrl());
                        }
                        if (result.getType().equals(SearchType.找人)) {
                            execSendSmsToFollow(this.context);
                        } else {
                            sendSmsToCommentFans(this.context);
                        }
                        if (this.context.getCounter() == this.context.getLimitNum()) {
                            this.context.setFinished(true);
                        }
                        ThreadUtil.sleepSeconds(15);
                    } else {
                        this.context.setFinished(true);
                    }
                }
            };
            taskList.add(task);
        }
        int cnt = taskList.size();
        int totalSendNum = 0;
        while (true) {
            int finished = 0;
            for (ActionTask task : taskList) {
                if (!task.getContext().isFinished()) {
                    task.run();
                    if (task.getContext().isFinished()) {
                        finished++;
                        totalSendNum += task.getContext().getCounter();
                    }
                }
            }
            if (finished == cnt) {
                break;
            }
        }
        lbResult.setText("本次共发送私信：" + totalSendNum);
    }

    public void execCrawlV() {
        String text = "";
        if (StringUtil.isEmpty(text)) {
            lbTip.setText("请输入大V关键词");
            return;
        }
        Tag tag = new Tag();
        tag.setWord(text);
        fansService.saveTag(tag);
        WebDriver driver = getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        try {
            driver.manage().window().maximize();
            String word = encode(text, "UTF-8");
            word = encode(word, "UTF-8");
            driver.get("http://s.weibo.com/user/" + word + "&Refer=weibo_user");

            int totalPage = driver.findElements(By.cssSelector(".W_pages .list .W_scroll ul li")).size();

            int page = 1;
            List<WbMember> vs = new ArrayList<>(totalPage * 20);
            while (true) {
                List<WebElement> elements;
                do {
                    ThreadUtil.sleep(500);
                    elements = driver.findElements(By.cssSelector(".pl_personlist .list_person"));
                } while (elements.isEmpty());
                List<WbMember> wbMembers = new ArrayList<>(elements.size());
                for (WebElement element : elements) {
                    WebElement he = element.findElement(By.cssSelector(".person_pic a img"));
                    WbMember wbMember = new WbMember();
                    wbMember.setHeadImg(he.getAttribute("src"));
                    wbMember.setId(Long.parseLong(he.getAttribute("uid")));
                    WebElement de_name = element.findElement(By.cssSelector(".person_detail .person_name a"));
                    wbMember.setNick(de_name.getAttribute("title"));
                    wbMember.setHome(de_name.getAttribute("href"));
                    WebElement de_addr = element.findElement(By.cssSelector(".person_detail .person_addr"));
                    String sexClass = de_addr.findElement(By.cssSelector("span:nth-child(1)")).getAttribute("class");
                    wbMember.setSex(sexClass.contains("female") ? "female" : "male");
                    wbMember.setAddr(de_addr.findElement(By.cssSelector("span:nth-child(2)")).getText());
                    wbMember.setvTagIds("," + tag.getId());
                    wbMember.setTagIds("");
                    try {
                        WebElement de_card = element.findElement(By.cssSelector(".person_detail .person_card"));
                        wbMember.setCard(de_card.getText());
                    } catch (Throwable ignore) {
                        wbMember.setCard("");
                    }
                    WebElement fansEl = element.findElement(By.cssSelector(".person_detail .person_num span:nth-child(2) a"));
                    String fansText = fansEl.getText();
                    if (fansText.endsWith("万")) {
                        wbMember.setFansNum(Integer.parseInt(fansText.replace("万", "")) * 10000);
                    } else {
                        wbMember.setFansNum(Integer.parseInt(fansText));
                    }
                    wbMember.setBigV(wbMember.getFansNum() > 10000);
                    wbMembers.add(wbMember);
                }
                fansService.saveWbMembers(AppContext.getMemberId(), wbMembers);
                vs.addAll(wbMembers);
                if (page >= totalPage) {
                    break;
                }
                nextPage(driver, je, ++page);
            }

            crawlVFans(vs, tag.getId(), driver, je);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crawlVFans(List<WbMember> vs, long tagId, WebDriver driver, JavascriptExecutor je) {
        for (WbMember v : vs) {
            driver.get(v.getHome());
            try {
                WebElement counterEL = driver.findElement(By.cssSelector(".PCD_counter"));
                List<WebElement> counterList = counterEL.findElements(By.cssSelector(".S_line1"));
                try {
                    String link = counterList.get(1).findElement(By.cssSelector(".t_link")).getAttribute("href");
                    driver.get(link);
                } catch (Throwable err) {
                    continue;
                }
            } catch (NoSuchElementException err) {
                driver.navigate().refresh();
            }
            int page = 1;
            while (true) {
                List<WebElement> fansList = driver.findElements(By.cssSelector(".follow_box .follow_list .follow_item"));
                if (fansList.size() == 0) {
                    break;
                }
                ThreadUtil.sleepSeconds(1);
                List<WbMember> fanss = new ArrayList<>();
                for (WebElement fans : fansList) {
                    WbMember wbMember = new WbMember();
                    WebElement we = fans.findElement(By.cssSelector(".mod_pic a"));
                    WebElement headEl = fans.findElement(By.cssSelector(".mod_pic a img"));
                    wbMember.setHeadImg(headEl.getAttribute("src"));
                    String ac = headEl.getAttribute("usercard");
                    ac = ac.replace("id=", "");
                    ac = ac.substring(0, ac.indexOf("&"));
                    wbMember.setId(Long.parseLong(ac));
                    wbMember.setNick(we.getAttribute("title"));
                    wbMember.setHome(we.getAttribute("href"));

                    String sexClass = fans.findElement(By.cssSelector(".mod_info .info_name a:nth-child(2)")).getAttribute("class");
                    wbMember.setSex(sexClass.contains("icon_female") ? "female" : "male");

                    WebElement de_addr = fans.findElement(By.cssSelector(".mod_info .info_add span"));
                    wbMember.setAddr(de_addr.getText());
                    wbMember.setTagIds("," + tagId);
                    wbMember.setvTagIds("");

                    try {
                        WebElement de_card = fans.findElement(By.cssSelector(".mod_info .info_intro span"));
                        wbMember.setCard(de_card.getText());
                    } catch (Throwable ignore) {
                        wbMember.setCard("");
                    }
                    WebElement fansEl = fans.findElement(By.cssSelector(".mod_info .info_connect span:nth-child(2) em a"));
                    String fansText = fansEl.getText();
                    if (fansText.endsWith("万")) {
                        wbMember.setFansNum(Integer.parseInt(fansText.replace("万", "")) * 10000);
                    } else {
                        wbMember.setFansNum(Integer.parseInt(fansText));
                    }
                    wbMember.setBigV(wbMember.getFansNum() > 10000);

                    fanss.add(wbMember);
                }
                fansService.saveWbMembers(AppContext.getMemberId(), fanss);
                if (page == 5) {
                    break;
                }
                try {
                    WebElement next = driver.findElement(By.cssSelector(".W_pages .next"));
                    if (!next.getAttribute("class").contains("page_dis")) {
                        je.executeScript("arguments[0].scrollIntoView(true);", next);
                        ActionUtil.moveToEl(driver, next);
                        next.click();
                        waitPage(driver, ++page);
                    } else {
                        break;
                    }
                } catch (NoSuchElementException err) {
                    break;
                }
            }
        }
    }

    private void nextPage(WebDriver driver, JavascriptExecutor je, int page) {
        WebElement next = driver.findElement(By.cssSelector(".W_pages .next"));
        if (!next.getAttribute("class").contains("page_dis")) {
            je.executeScript("arguments[0].scrollIntoView(true);", next);
            ActionUtil.moveToEl(driver, next);
            next.click();
            do {
                try {
                    WebElement pageEl = driver.findElement(By.cssSelector(".W_pages .list .page"));
                    if (pageEl.getText().startsWith("第" + page + "页")) {
                        return;
                    }
                    ThreadUtil.sleep(500);
                } catch (Throwable e) {
                    ThreadUtil.sleep(500);
                }
            } while (true);
        }
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        if (CollectionUtil.isNotEmpty(searchResults)) {
            queue.addAll(searchResults);
            ObservableList<String> items = FXCollections.observableArrayList(Collections2.transform(searchResults, SearchResult::getUrl));
            listView.setItems(items);
        }
    }

    /**
     * 对粉丝列表发私信(只能抓取前5页大概100条数据)
     *
     * @param context
     */
    private void execSendSmsToFollow(TaskContext context) {
        WebDriver driver = context.getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        if (context.isNext()) {
            try {
                WebElement counterEL = driver.findElement(By.cssSelector(".PCD_counter"));
                List<WebElement> counterList = counterEL.findElements(By.cssSelector(".S_line1"));
                try {
                    String link = counterList.get(1).findElement(By.cssSelector(".t_link")).getAttribute("href");
                    if (context.getPage() == 1) {
                        driver.get(link);
                    } else {
                        String url = link.substring(0, link.indexOf('?')) + "page=" + context.getPage() + "&" + link.substring(link.indexOf('?') + 1);
                        driver.get(url);
                    }
                } catch (Throwable err) {
                    return;
                }
            } catch (NoSuchElementException err) {
                driver.navigate().refresh();
            }
        }
        context.setNext(false);
        while (true) {
            List<WebElement> fansList = driver.findElements(By.cssSelector(".follow_box .follow_list .follow_item:nth-child(n+" + context.getCnter() + ")"));
            if (fansList.size() == 0) {
                break;
            }
            ThreadUtil.sleepSeconds(1);
            for (WebElement fans : fansList) {
                WebElement headEl = fans.findElement(By.cssSelector(".mod_pic a img"));
                String ac = headEl.getAttribute("usercard");
                if (!context.getSet().add(ac)) {
                    continue;
                }
                context.incrCounter();
                while (true) {
                    try {
                        //沉淀粉丝
                        WbMember wbMember = new WbMember();
                        WebElement we = fans.findElement(By.cssSelector(".mod_pic a"));
                        wbMember.setHeadImg(headEl.getAttribute("src"));
                        ac = ac.replace("id=", "");
                        if (ac.indexOf('&') != -1) {
                            ac = ac.substring(0, ac.indexOf("&"));
                        }
                        wbMember.setId(Long.parseLong(ac));
                        wbMember.setNick(we.getAttribute("title"));
                        wbMember.setHome(we.getAttribute("href"));

                        String sexClass = fans.findElement(By.cssSelector(".mod_info .info_name a:nth-child(2)")).getAttribute("class");
                        wbMember.setSex(sexClass.contains("icon_female") ? "female" : "male");

                        WebElement de_addr = fans.findElement(By.cssSelector(".mod_info .info_add span"));
                        wbMember.setAddr(de_addr.getText());
                        wbMember.setTagIds("");
                        wbMember.setvTagIds("");

                        try {
                            WebElement de_card = fans.findElement(By.cssSelector(".mod_info .info_intro span"));
                            wbMember.setCard(de_card.getText());
                        } catch (Throwable ignore) {
                            wbMember.setCard("");
                        }
                        WebElement fansEl = fans.findElement(By.cssSelector(".mod_info .info_connect span:nth-child(2) em a"));
                        String fansText = fansEl.getText();
                        if (fansText.endsWith("万")) {
                            wbMember.setFansNum(Integer.parseInt(fansText.replace("万", "")) * 10000);
                        } else {
                            wbMember.setFansNum(Integer.parseInt(fansText));
                        }
                        wbMember.setBigV(wbMember.getFansNum() > 10000);
                        context.getWbMembers().add(wbMember);
                        //沉淀粉丝结束

                        WebElement e = fans.findElement(By.cssSelector(".opt_box a[action-type=opt_box_more]"));
                        ActionUtil.moveToEl(driver, e);
                        ThreadUtil.sleepSeconds(1);
                        WebElement el2 = fans.findElement(By.cssSelector(".layer_menu_list a[action-type='webim.conversation']"));
                        je.executeScript("arguments[0].scrollIntoView(false);", el2);
                        ActionUtil.moveToEl(driver, el2);
                        el2.click();
                        ThreadUtil.sleepSeconds(1);
                        WebElement wechatEl = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
                        ActionUtil.moveToEl(driver, wechatEl);
                        ThreadUtil.sleepSeconds(2);
                        System.out.println(wechatEl.getText());
                        WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
                        input.sendKeys(txtContent.getText());
                        ThreadUtil.sleep(100);
                        input.sendKeys(Keys.ENTER);
                        ThreadUtil.sleep(100);
                        break;
                    } catch (Exception nve) {
                        ThreadUtil.sleepSeconds(1);
                    }
                }
                while (true) {
                    try {
                        WebElement w = driver.findElement(By.cssSelector(".yzm_input"));
                        ThreadUtil.sleepSeconds(3);
                    } catch (Throwable thr) {
                        break;
                    }
                }
                ThreadUtil.sleepSeconds(1);
                ActionUtil.click(driver, ".webim_chat_window .chat_head .W_ficon.ficon_close");
                if (context.getCounter() % 5 == 0 || context.getCounter() == context.getLimitNum()) {
                    return;
                } else {
                    ThreadUtil.sleepSeconds(RandomUtil.randomInt(5));
                }
            }
            context.incrPage();
            if (context.getPage() > 5) {
                context.setNext(true);
                break;
            }
            WebElement next = driver.findElement(By.cssSelector(".W_pages .next"));
            if (!next.getAttribute("class").contains("page_dis")) {
                ActionUtil.moveToEl(driver, next);
                next.click();
                waitPage(driver, context.getPage());
            } else {
                context.setNext(true);
                break;
            }
        }
    }

    private void waitPage(WebDriver driver, int page) {
        do {
            try {
                WebElement pageEl = driver.findElement(By.cssSelector(".W_pages .page.S_bg1"));
                if (Integer.parseInt(pageEl.getText()) == page) {
                    break;
                }
                ThreadUtil.sleep(500);
            } catch (Throwable e) {
                ThreadUtil.sleep(500);
            }
        } while (true);
    }

    /**
     * 抓取我的微博下评论的粉丝
     *
     * @param context
     */
    private void sendSmsToCommentFans(TaskContext context) {
        WebDriver driver = context.getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        context.setNext(false);
        try {
            while (true) {
                List<WebElement> list;
                ThreadUtil.sleepSeconds(2);
                int mnt = 0;
                while (true) {
                    mnt++;
                    list = driver.findElements(By.cssSelector(".repeat_list .list_ul .list_li:nth-child(n+" + context.getCounter() + ")"));
                    if (list.size() > 0 || mnt > 3) break;
                    ThreadUtil.sleep(500);
                }
                for (WebElement comment : list) {
                    WebElement face = comment.findElement(By.cssSelector(".WB_text a"));
                    if (!context.getSet().add(face.getAttribute("usercard"))) {
                        continue;
                    }
                    context.incrCounter();
                    String href = face.getAttribute("href");
                    if (!driver.getCurrentUrl().startsWith(href)) {
                        while (true) {
                            try {
                                je.executeScript("arguments[0].scrollIntoView(false);", face);
                                ActionUtil.moveToEl(driver, face);
                                ThreadUtil.sleep(500);
                                WebElement fcEl = driver.findElement(By.cssSelector(".W_layer_pop .layer_personcard .c_btnbox a:nth-child(3)"));
                                fcEl.click();
                                WebElement wechatEl = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
                                ActionUtil.moveToEl(driver, wechatEl);
                                ThreadUtil.sleepSeconds(2);
                                System.out.println(wechatEl.getText());
                                WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
                                input.sendKeys(txtContent.getText());
                                ThreadUtil.sleep(100);
                                input.sendKeys(Keys.ENTER);
                                ThreadUtil.sleep(100);
                                ActionUtil.moveToEl(driver, face);
                                break;
                            } catch (Exception e) {
                                ThreadUtil.sleep(500);
                            }
                        }
                        while (true) {
                            try {
                                WebElement w = driver.findElement(By.cssSelector(".yzm_input"));
                                ThreadUtil.sleepSeconds(3);
                            } catch (Throwable thr) {
                                break;
                            }
                        }
                        ThreadUtil.sleepSeconds(1);
                        ActionUtil.click(driver, ".webim_chat_window .chat_head .W_ficon.ficon_close");
                    }
                    if (context.getCounter() % 5 == 0 || context.getCounter() == context.getLimitNum()) {
                        return;
                    } else {
                        ThreadUtil.sleepSeconds(RandomUtil.randomInt(5));
                    }
                }

                try {
                    WebElement next = driver.findElement(By.cssSelector("a[action-type='click_more_comment']"));
                    next.click();
                } catch (Exception e) {
                    list = driver.findElements(By.cssSelector(".repeat_list .list_ul .list_li:nth-child(n+" + (context.getCounter() + 1) + ")"));
                    if (list.size() == 0) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebDriver getDriver() {
        WebDriver driver = this.chromeBrowserService.getDriver();
        try {
            if (driver != null) {
//                driver.get(uri);
//                ThreadUtil.sleepSeconds(3);
//                driver.findElements(By.cssSelector(".WB_feed_comment .WB_cardwrap[node-type=feed_commentList_comment]"));
            } else {
                driver = loginDriver();
                Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
                driver.manage().window().setSize(new org.openqa.selenium.Dimension(screensize.width, screensize.height));
            }
        } catch (Throwable e) {
            driver = loginDriver();
            Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(screensize.width, screensize.height));
        }
        return driver;
    }

    private WebDriver loginDriver() {
        WebDriver driver;
        this.chromeBrowserService.login(AppContext.getMember());
        ThreadUtil.sleepSeconds(1);
        while (true) {
            driver = this.chromeBrowserService.getDriver();
            String uri = driver.getCurrentUrl();
            if (!uri.startsWith("https://weibo.com/u") && !uri.endsWith("/home")) {
                ThreadUtil.sleepSeconds(3);
            } else {
                break;
            }
        }
        return driver;
    }

}
