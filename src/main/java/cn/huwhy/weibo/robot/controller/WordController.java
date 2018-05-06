package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.model.Word;
import cn.huwhy.weibo.robot.model.WordTerm;
import cn.huwhy.weibo.robot.model.WordType;
import cn.huwhy.weibo.robot.service.WordService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import cn.huwhy.weibo.robot.AppContext;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class WordController extends BaseController implements Initializable {

    @FXML
    private TextField txWord;
    @FXML
    private ChoiceBox<WordType> chbType;
    @FXML
    private Text actionTarget;
    @FXML
    private TableView<Word> tableWords;
    @FXML
    private Button pagePre, pageCur, pageNext;
    @FXML
    private Label lbWordNum;

    private WordService wordService;
    private Word word;
    private WordTerm term;

    public void chbTypeChange() {
        WordType type = chbType.getValue();
        loadWord(1, type);
    }

    public void save() {
        if (StringUtil.isEmpty(txWord.getText()) || chbType.getValue() == null) {
            actionTarget.setText("数据请填写完成");
        } else {
            WordType type = chbType.getValue();
            String text = txWord.getText();
            if (word == null) {
                word = new Word();
            }
            word.setWord(text);
            word.setType(type);
            word.setMemberId(AppContext.getMemberId());
            wordService.save(word);
            getParent().refresh();
            AppContext.closeModel();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wordService = SpringContentUtil.getBean(WordService.class);
        if (chbType != null) {
            chbType.setItems(FXCollections.observableArrayList(WordType.values()));
        }
        if (tableWords != null) {
            term = new WordTerm();
            tableWords.getColumns().clear();
            TableColumn<Word, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableWords.getColumns().add(colId);

            TableColumn<Word, Integer> colWord = new TableColumn<>("敏感词");
            colWord.setCellValueFactory(new PropertyValueFactory<>("word"));
            tableWords.getColumns().add(colWord);

            TableColumn<Word, String> colType = new TableColumn<>("类型");
            colType.setCellValueFactory(new PropertyValueFactory<Word, String>("type") {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Word, String> param) {
                    return new ReadOnlyObjectWrapper<>(param.getValue().getType().getName());
                }
            });
            tableWords.getColumns().add(colType);
            TableColumn<Word, Integer> colNum = new TableColumn<>("匹配次数");
            colNum.setCellValueFactory(new PropertyValueFactory<>("hitNum"));
            tableWords.getColumns().add(colNum);
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

    public void setWord(Word word) {
        this.word = word;
        txWord.setText(word.getWord());
        chbType.setValue(word.getType());
    }

    public void showAddWord() {
        try {
            WordController controller = AppContext.showModel("word/word.fxml");
            controller.setParent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditWord() {
        Word word = tableWords.getSelectionModel().getSelectedItem();
        try {
            WordController controller = AppContext.showModel("word/word.fxml");
            controller.setWord(word);
            controller.setParent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delWord() {
        Word word = tableWords.getSelectionModel().getSelectedItem();
        tableWords.getItems().remove(word);
        tableWords.refresh();
        wordService.delete(word.getId());
    }

    public void reloadWord() {
        loadWord(1, null);
        if (chbType != null) {
            chbType.setValue(null);
        }
    }

    private void loadWord(int page, WordType type) {
        WordTerm term = new WordTerm();
        term.setPage(page);
        term.setSize(20);
        term.setType(type);
        term.setMemberId(AppContext.getMemberId());
        Paging<Word> paging = wordService.findWords(term);
        ObservableList<Word> words = FXCollections.observableArrayList(paging.getData());
        tableWords.setItems(words);
        tableWords.refresh();
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

    @Override
    public void refresh() {
        reloadWord();
    }
}
