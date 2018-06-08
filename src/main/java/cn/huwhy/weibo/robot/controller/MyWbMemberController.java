package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.FansDto;
import cn.huwhy.weibo.robot.model.FansTerm;
import cn.huwhy.weibo.robot.model.MyFans;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.common.WbAccountTerm;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.service.WbAccountService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyWbMemberController extends BaseController implements Initializable {

    private FansService fansService;

    /*查询 start*/
    @FXML
    private FansTerm term;
    /*查询 end*/

    /*表格 start*/
    @FXML
    private TableView<FansDto> tableView;
    /*表格 end*/

    /*分页 start*/
    @FXML
    private Button pagePre, pageCur, pageNext;
    @FXML
    private Label lbTotal;
    /*分页 end*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fansService = SpringContentUtil.getBean(FansService.class);
        if (tableView != null) {
            initTableColumns();
            initPaging();
        }
    }

    public void queryEvent() {
        term.setPage(1);
        loadData(term);
    }

    @Override
    public void refresh() {
        loadData(term);
    }

    @Override
    public void windowsClose() {
        System.out.println("model windows closed");
    }

    private void initTableColumns() {
        TableColumn<FansDto, CheckBox> colCk = new TableColumn<>("选择");
        colCk.setCellValueFactory(cellData -> cellData.getValue().getCb().getCheckBox());
        TableColumn<FansDto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<FansDto, String> colUsername = new TableColumn<>("微博帐号");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("nick"));
        colUsername.setMinWidth(100);
        TableColumn<FansDto, String> colHome = new TableColumn<>("主页");
        colHome.setMinWidth(100);
        colHome.setCellValueFactory(new PropertyValueFactory<>("home"));
        tableView.getColumns().addAll(colCk, colId, colUsername, colHome);
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    URI uri = new URI(tableView.getSelectionModel().getSelectedItem().getHome());
                    Desktop.getDesktop().browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initPaging() {
        term = new FansTerm();
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

    private void loadData(FansTerm term) {
        Paging<FansDto> paging = fansService.findFans(term);
        ObservableList<FansDto> list = FXCollections.observableArrayList(paging.getData());
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
