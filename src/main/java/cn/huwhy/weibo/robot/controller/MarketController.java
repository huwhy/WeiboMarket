package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.DateUtil;
import cn.huwhy.common.util.StringUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.action.ActionUtil;
import cn.huwhy.weibo.robot.action.CommentAction;
import cn.huwhy.weibo.robot.model.MemberConfig;
import cn.huwhy.weibo.robot.model.Task;
import cn.huwhy.weibo.robot.model.TaskStatus;
import cn.huwhy.weibo.robot.model.TaskTerm;
import cn.huwhy.weibo.robot.service.ChromeBrowserService;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.service.TaskService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static javafx.collections.FXCollections.observableArrayList;

public class MarketController extends BaseController implements Initializable {

    @FXML
    private TextArea txtContent;
    @FXML
    private TextField txWord;
    @FXML
    private Label lbTip, lbWordNum;
    @FXML
    private TableView<Task> tableView;
    @FXML
    private Button pagePre, pageCur, pageNext;

    private ChromeBrowserService chromeBrowserService;
    private TaskService taskService;
    private TaskTerm term;

    public void init() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chromeBrowserService = SpringContentUtil.getBean(ChromeBrowserService.class);
        taskService = SpringContentUtil.getBean(TaskService.class);
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

        tableView.getColumns().addAll(colId, colName, colStartTime, colStatus, colSummary, colEndTime);
        pagePre.setOnAction(event -> {
            int curPage = Integer.parseInt(pageCur.getText());
            System.out.println("curPage:" + curPage);
            loadData(curPage - 1, term.getStatus());
        });
        pageNext.setOnAction(event -> {
            int curPage = Integer.parseInt(pageCur.getText());
            System.out.println("curPage:" + curPage);
            loadData(curPage + 1, term.getStatus());
        });
        term = new TaskTerm();
        loadData(1, term.getStatus());

    }

    public void execCommentTask() {
        if (StringUtil.isEmpty(txWord.getText())) {
            lbTip.setText("请输入大V关键词");
            return;
        }
        if (StringUtil.isEmpty(txtContent.getText())) {
            lbTip.setText("请输入私信内容");
            return;
        }
        lbTip.setText("");
        WebDriver driver = getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        try {
            driver.manage().window().maximize();
            String word = URLEncoder.encode(txWord.getText(), "UTF-8");
            word = URLEncoder.encode(word, "UTF-8");
            driver.get("http://s.weibo.com/user/" + word + "&Refer=weibo_user");
            List<WebElement> elements = driver.findElements(By.cssSelector(".pl_personlist .list_person"));
            System.out.println(elements.size());
            WebElement el = elements.get(0);
            WebElement linkEl = el.findElement(By.cssSelector(".W_linkb"));
            driver.get(linkEl.getAttribute("href"));
//            System.out.println(linkEl.getAttribute("href"));
            WebElement counterEL = driver.findElement(By.cssSelector(".PCD_counter"));
            List<WebElement> counterList = counterEL.findElements(By.cssSelector(".S_line1"));
            String link = counterList.get(1).findElement(By.cssSelector(".t_link")).getAttribute("href");
            System.out.println(link);
            driver.get(link);
            List<WebElement> fansList = driver.findElements(By.cssSelector(".follow_box .follow_list .follow_item"));
            for (WebElement fans : fansList) {
                WebElement e = fans.findElement(By.cssSelector(".opt_box a[action-type=opt_box_more]"));
                ActionUtil.moveToEl(driver, e);
                WebElement el2 = fans.findElement(By.cssSelector(".layer_menu_list a[action-type='webim.conversation']"));
                ActionUtil.moveToEl(driver, el2);
                ThreadUtil.sleepSeconds(1);
                el2.click();
                ThreadUtil.sleepSeconds(1);
                WebElement wechatEl = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
                ActionUtil.moveToEl(driver, wechatEl);
                ThreadUtil.sleepSeconds(2);
                System.out.println(wechatEl.getText());
                WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
                input.sendKeys(txtContent.getText());
                input.sendKeys(Keys.ENTER);
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
                je.executeScript("arguments[0].scrollIntoView(true);", fans);
            }
//            String mainHandler = driver.getWindowHandle();
//            fans.get(0).findElement(By.cssSelector(".mod_pic")).click();
//            for(String handler : driver.getWindowHandles()) {
//                if (handler.equals(mainHandler)) {
//                    continue;
//                }
//                driver.switchTo().window(handler);
//            }
//            List<WebElement> list = driver.findElements(By.cssSelector(".PCD_header .pf_opt .btn_bed"));
//            ThreadUtil.sleepSeconds(1);
//            list.get(1).findElement(By.tagName("a")).click();
//            ThreadUtil.sleepSeconds(1);
//            WebElement e = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
//            System.out.println(e.getText());
//            WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
//            input.sendKeys("您好啊； https://weibo.com");
//            driver.switchTo().window(mainHandler);
//            fans.get(1).findElement(By.cssSelector(".mod_pic")).click();
//            driver.switchTo().window(mainHandler);
//            driver.get("https://weibo.com/u/3721328217");
//            ThreadUtil.sleepSeconds(1);
//            input.sendKeys("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        loadData(1, term.getStatus());
    }

    private void loadData(int page, TaskStatus status) {
        term.setPage(page);
        term.setSize(20);
        term.setStatus(status);
        term.setMemberId(AppContext.getMemberId());
        Paging<Task> paging = taskService.findTasks(term);
        ObservableList<Task> tasks = observableArrayList(paging.getData());
        tableView.setItems(tasks);
        tableView.refresh();
        lbWordNum.setText("总记录数: " + paging.getTotal() + " 共" + paging.getTotalPage() + "页");
        if (page == 1) {
            pagePre.setDisable(true);
        } else {
            pagePre.setDisable(false);
        }
        pageCur.setText(page + "");
        if (page == term.getTotalPage()) {
            pageNext.setDisable(true);
        } else {
            pageNext.setDisable(false);
        }
    }

    private static String uri = "https://weibo.com/comment/inbox?topnav=1&wvr=6&f=1";

    private WebDriver getDriver() {
        WebDriver driver = this.chromeBrowserService.getDriver();
        try {
            if (driver != null) {
                driver.get(uri);
                ThreadUtil.sleepSeconds(3);
                driver.findElements(By.cssSelector(".WB_feed_comment .WB_cardwrap[node-type=feed_commentList_comment]"));
            } else {
                driver = loginDriver();
            }
        } catch (Throwable e) {
            driver = loginDriver();
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
//            if (!driver.getCurrentUrl().startsWith("https://weibo.com/lixiaolu/home")) {
//            if (!driver.getCurrentUrl().startsWith("https://weibo.com/lixiaolu/home")) {
                ThreadUtil.sleepSeconds(3);
            } else {
                break;
            }
        }
        return driver;
    }
}
