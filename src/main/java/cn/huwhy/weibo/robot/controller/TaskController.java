package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.DateUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.action.CommentAction;
import cn.huwhy.weibo.robot.model.MemberConfig;
import cn.huwhy.weibo.robot.model.Task;
import cn.huwhy.weibo.robot.model.TaskStatus;
import cn.huwhy.weibo.robot.model.TaskTerm;
import cn.huwhy.weibo.robot.service.ChromeBrowserService;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.service.TaskService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import cn.huwhy.weibo.robot.AppContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class TaskController extends BaseController implements Initializable {

    @FXML
    private DatePicker dpDate, dpDate2;
    @FXML
    private TextField txBlackNum;
    @FXML
    private CheckBox cbOpenBlack;
    @FXML
    private TableView<Task> tableView;
    @FXML
    private Button pagePre, pageCur, pageNext;
    @FXML
    private Label lbWordNum, lbAutoTip;
    @FXML
    private Button btnExec, btnAutoExec;
    @FXML
    private Text actionTarget;

    private CommentAction commentAction;
    private ChromeBrowserService chromeBrowserService;
    private TaskService taskService;
    private MemberService memberService;
    private TaskTerm term;

    public void init() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentAction = SpringContentUtil.getBean(CommentAction.class);
        chromeBrowserService = SpringContentUtil.getBean(ChromeBrowserService.class);
        memberService = SpringContentUtil.getBean(MemberService.class);
        taskService = SpringContentUtil.getBean(TaskService.class);
        if (dpDate != null) {
            dpDate.setValue(LocalDate.now().plusDays(-7));
            dpDate2.setValue(LocalDate.now());
            txBlackNum.setText(AppContext.getMember().getConfig().getBadNumLimit() + "");
            cbOpenBlack.setSelected(AppContext.getMember().getConfig().isOpenBlack());
        }
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
        run();
    }

    public void execAutoCommentTask() {
        taskService.submit(this::run, 60 * 60);
    }

    private void run() {
        CommentAction action = SpringContentUtil.getBean(CommentAction.class);
        WebDriver driver = getDriver();
        MemberConfig config = AppContext.getMember().getConfig();
        config.setOpenBlack(cbOpenBlack.isSelected());
        config.setBadNumLimit(Integer.parseInt(txBlackNum.getText()));
        memberService.save(AppContext.getMember());
        LocalDate value = dpDate.getValue();
        LocalDate value2 = dpDate2.getValue();
        if (value == null || value2 == null) {
            actionTarget.setText("请输入日期区间");
        }
        Instant instant = value.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant instant2 = value2.atStartOfDay(ZoneId.systemDefault()).toInstant();
        action.init(driver, AppContext.getMember(), Date.from(instant), Date.from(instant2));
        action.run(this::refresh);
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
            if (!uri.startsWith("https://weibo.com/u") && !uri.endsWith("/home")){
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
