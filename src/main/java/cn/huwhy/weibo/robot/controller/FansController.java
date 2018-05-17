package cn.huwhy.weibo.robot.controller;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.MyFans;
import cn.huwhy.weibo.robot.model.MyFansTerm;
import cn.huwhy.weibo.robot.model.WordType;
import cn.huwhy.weibo.robot.service.WbFansService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class FansController extends BaseController implements Initializable {

    @FXML
    private ChoiceBox<WordType> chbType;
    @FXML
    private TableView<MyFans> tableView;
    @FXML
    private Button pagePre, pageCur, pageNext;
    @FXML
    private Label lbWordNum;

    private WbFansService wbFansService;
    private MyFansTerm term;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wbFansService = SpringContentUtil.getBean(WbFansService.class);

        if (chbType != null) {
            chbType.setItems(FXCollections.observableArrayList(WordType.values()));
        }
        if (tableView != null) {
            term = new MyFansTerm();
            tableView.getColumns().clear();

            TableColumn<MyFans, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<MyFans, String> colNick = new TableColumn<>("昵称");
            colNick.setCellValueFactory(new PropertyValueFactory<>("nick"));
            TableColumn<MyFans, String> colType = new TableColumn<>("类型");
            colType.setCellValueFactory(new PropertyValueFactory<MyFans, String>("type") {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<MyFans, String> param) {
                    return new ReadOnlyObjectWrapper<>(param.getValue().getType().getName());
                }
            });
            TableColumn<MyFans, String> colHome = new TableColumn<>("主页");
            colHome.setCellFactory(param -> {
                TextFieldTableCell<MyFans, String> cell = new TextFieldTableCell<>();
                cell.setOnMouseClicked((MouseEvent t) -> {
                    if (t.getClickCount() == 2) {
                        try {
                            URI uri = new URI(((TextFieldTableCell) t.getTarget()).getText());
                            Desktop.getDesktop().browse(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return cell;
            });
            colHome.setCellValueFactory(new PropertyValueFactory<>("home"));
            TableColumn<MyFans, String> colHeadImg = new TableColumn<>("头像");
//            colHeadImg.setCellFactory(param -> {
//                TextFieldTableCell<MyFans, String> cell = new TextFieldTableCell<MyFans, String>() {
//                    public void updateItem(String item, boolean empty) {
//                        super.updateItem("", empty);
//                        if (StringUtil.isNotEmpty(item)) {
//                            this.setStyle("-fx-background-image: url('" + item + "')");
////                            BackgroundImage backgroundImage = new BackgroundImage(new Image(item),
////                                    BackgroundRepeat.NO_REPEAT,
////                                    BackgroundRepeat.NO_REPEAT,
////                                    BackgroundPosition.CENTER,
////                                    BackgroundSize.DEFAULT);
////                            this.setBackground(new Background(backgroundImage));
//                        }
//                    }
//                };
//                cell.setMinHeight(50);
//                cell.setPrefHeight(50);
//                return cell;
//            });
//            colHeadImg.setCellValueFactory(new PropertyValueFactory<>("headImg"));

            tableView.getColumns().addAll(colId, colNick, colType, colHome);
            loadWord(1, term.getType());
            pagePre.setOnAction(event -> {
                int curPage = Integer.parseInt(pageCur.getText());
                System.out.println("curPage:" + curPage);
                loadWord(curPage - 1, term.getType());
            });
            pageNext.setOnAction(event -> {
                int curPage = Integer.parseInt(pageCur.getText());
                System.out.println("curPage:" + curPage);
                loadWord(curPage + 1, term.getType());
            });
        }
    }

    private void loadWord(int page, WordType type) {
        term.setPage(page);
        term.setSize(20);
        term.setType(type);
        term.setMemberId(AppContext.getMemberId());
        Paging<MyFans> paging = wbFansService.findMyFans(term);
        ObservableList<MyFans> items = FXCollections.observableArrayList(paging.getData());
        tableView.setItems(items);
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

    @FXML
    public void tableViewOnClick() {
        MyFans fans = tableView.getSelectionModel().getSelectedItem();
        System.out.println(fans);
    }

    public void reloadWord() {
        loadWord(1, null);
        if (chbType != null) {
            chbType.setValue(null);
        }
    }

    public void chbTypeChange() {
        WordType type = chbType.getValue();
        loadWord(1, type);
    }
}
