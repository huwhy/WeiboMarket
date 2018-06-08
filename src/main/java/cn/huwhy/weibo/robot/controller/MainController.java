package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.Base64;
import cn.huwhy.common.util.StringUtil;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends BaseController implements Initializable {

    private String code;

    @FXML
    private TextField txWbName, txCode;
    @FXML
    private PasswordField txWbPwd;
    @FXML
    private Label lbCode;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabWb, tabWords, tabTask, tabSearch, tabMarket, tabMyFans, tabFans, tabData, tabMy;
    private MemberService memberService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        memberService = SpringContentUtil.getBean(MemberService.class);
        AppContext.setMainController(this);
        initTabs();
    }

    private void initTabs() {
        if (tabWb.getContent() == null) {
            Parent parent = AppContext.loadFxml("wb_account/index.fxml");
            tabWb.setContent(parent);
        }
        if (tabWords.getContent() == null) {
            Parent parent = AppContext.loadFxml("word/list.fxml");
            tabWords.setContent(parent);
        }
        if (tabMyFans.getContent() == null) {
            Parent parent = AppContext.loadFxml("fans/my_fans.fxml");
            tabMyFans.setContent(parent);
        }
        if (tabFans.getContent() == null) {
            Parent parent = AppContext.loadFxml("fans/list.fxml");
            tabFans.setContent(parent);
        }
        if (tabTask.getContent() == null) {
            Parent parent = AppContext.loadFxml("task/index.fxml");
            tabTask.setContent(parent);
        }
        if (tabSearch.getContent() == null) {
            Parent parent = AppContext.loadFxml("market/search.fxml");
            tabSearch.setContent(parent);
        }
        if (tabMarket.getContent() == null) {
            Parent parent = AppContext.loadFxml("market/sms.fxml");
            tabMarket.setContent(parent);
        }
        if (tabData.getContent() == null) {
            AppContext.setAutoTask(true);
            Parent parent = AppContext.loadFxml("data/index.fxml");
            tabData.setContent(parent);
        }
        if (tabMy.getContent() == null) {
            Parent parent = AppContext.loadFxml("my/setting.fxml");
            tabMy.setContent(parent);
        }
    }

    public void activeMarket() {
        tabPane.getSelectionModel().select(2);
    }

}
