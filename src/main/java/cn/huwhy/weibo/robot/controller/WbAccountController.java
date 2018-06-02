package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.SearchResult;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.common.WbAccountTerm;
import cn.huwhy.weibo.robot.service.WbAccountService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WbAccountController extends BaseController implements Initializable {

    private WbAccountService wbAccountService;

    /*查询 start*/
    @FXML
    private TextField txUsername;
    private WbAccountTerm term;
    /*查询 end*/

    /*表格 start*/
    @FXML
    private TableView<WbAccount> tableView;
    /*表格 end*/

    /*分页 start*/
    @FXML
    private Button pagePre, pageCur, pageNext;
    @FXML
    private Label lbTotal;
    /*分页 end*/

    /*添加编辑 start*/
    private WbAccount wbAccount;
    @FXML
    private Text txTitle, txTip;
    @FXML
    private TextField txUsername2;
    @FXML
    private PasswordField txPassword;
    /*添加编辑 end*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wbAccountService = SpringContentUtil.getBean(WbAccountService.class);
        if (tableView != null) {
            initTableColumns();
            initPaging();
        }
    }

    public void queryEvent() {
        term.setPage(1);
        term.setUsername(txUsername.getText());
        loadData(term);
    }

    public void addEvent() {
        WbAccountController controller = showAdd();
        controller.wbAccount = new WbAccount();
        controller.wbAccount.setMemberId(AppContext.getMemberId());
        controller.txTitle.setText("添加微博帐号");
    }

    public void editEvent() {
        WbAccount wbAccount = tableView.getSelectionModel().getSelectedItem();
        WbAccountController controller = showAdd();
        controller.wbAccount = wbAccount;
        controller.txTitle.setText("编辑微博帐号");
        controller.txUsername2.setText(wbAccount.getUsername());
        controller.txPassword.setText(wbAccount.getPassword());
    }

    public void saveWbAccount() {
        String username = txUsername2.getText();
        String password = txPassword.getText();
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            this.txTip.setText("数据不能为空");
        }
        wbAccount.setUsername(username);
        wbAccount.setPassword(password);
        this.wbAccountService.save(wbAccount);
        this.getParent().refresh();
        AppContext.closeModel();
    }

    public void del() {
        List<Integer> results = new ArrayList<>();
        for (WbAccount account : tableView.getItems()) {
            if (account.getCb().isSelected()) {
                results.add(account.getId());
            }
        }
        if (!results.isEmpty()) {
            this.wbAccountService.delByIds(results);
            refresh();
        }
    }

    @Override
    public void refresh() {
        loadData(term);
    }

    @Override
    public void windowsClose() {
        this.wbAccount = null;
        System.out.println("model windows closed");
    }

    private WbAccountController showAdd() {
        try {
            WbAccountController controller = AppContext.showModel("wb_account/edit.fxml");
            controller.setParent(this);
            return controller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initTableColumns() {
        TableColumn<WbAccount, CheckBox> colCk = new TableColumn<>("选择");
        colCk.setCellValueFactory(cellData -> cellData.getValue().getCb().getCheckBox());
        TableColumn<WbAccount, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<WbAccount, String> colUsername = new TableColumn<>("微博帐号");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setMinWidth(100);
        tableView.getColumns().addAll(colCk, colId, colUsername);
    }

    private void initPaging() {
        term = new WbAccountTerm();
        term.setSize(20);
        term.setPage(1);
        term.setMemberId(AppContext.getMemberId());

        pagePre.setOnAction(event -> {
            int curPage = Integer.parseInt(pageCur.getText());
            term.setPage(curPage - 1);
            loadData(term);
        });
        pageNext.setOnAction(event -> {
            int curPage = Integer.parseInt(pageCur.getText());
            term.setPage(curPage + 1);
            loadData(term);
        });
    }

    private void loadData(WbAccountTerm term) {
        Paging<WbAccount> paging = wbAccountService.findPaging(term);
        ObservableList<WbAccount> list = FXCollections.observableArrayList(paging.getData());
        tableView.setItems(list);
        tableView.refresh();
        lbTotal.setText("总记录数: " + paging.getTotal() + " 共" + paging.getTotalPage() + "页");
        if (term.getPage() <= 1) {
            pagePre.setDisable(true);
        } else {
            pagePre.setDisable(false);
        }
        pageCur.setText(term.getPage() + "");
        if (term.getPage() >= term.getTotalPage()) {
            pageNext.setDisable(true);
        } else {
            pageNext.setDisable(false);
        }
    }
}
