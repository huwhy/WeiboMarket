package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.interfaces.Term;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.action.ActionUtil;
import cn.huwhy.weibo.robot.dao.SearchResultDao;
import cn.huwhy.weibo.robot.model.SearchResult;
import cn.huwhy.weibo.robot.model.SearchResultTerm;
import cn.huwhy.weibo.robot.model.Tag;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.WbMember;
import cn.huwhy.weibo.robot.model.common.SearchType;
import cn.huwhy.weibo.robot.service.ChromeBrowserService;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.service.WbAccountService;
import cn.huwhy.weibo.robot.task.ActionTask;
import cn.huwhy.weibo.robot.task.TaskContext;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static java.net.URLEncoder.encode;
import static javafx.collections.FXCollections.observableArrayList;

public class SearchController extends BaseController implements Initializable {

    @FXML
    private TextField txWord;
    @FXML
    private ChoiceBox chbType;
    @FXML
    private TableView<SearchResult> tableView;
    @FXML
    private Label lbTip, lbTotal;
    private SearchResultTerm term = new SearchResultTerm();

    private ChromeBrowserService chromeBrowserService;
    private FansService fansService;
    private WbAccountService wbAccountService;
    private SearchResultDao searchResultDao;

    private WbAccount wbAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (chbType != null) {
            chbType.setItems(FXCollections.observableArrayList(SearchType.values()));
        }
        fansService = SpringContentUtil.getBean(FansService.class);
        chromeBrowserService = SpringContentUtil.getBean(ChromeBrowserService.class);
        wbAccountService = SpringContentUtil.getBean(WbAccountService.class);
        searchResultDao = SpringContentUtil.getBean(SearchResultDao.class);
        List<WbAccount> wbAccounts = wbAccountService.getByMemberId(AppContext.getMember().getId(), 1);
        if (wbAccounts.size() > 0) {
            this.wbAccount = wbAccounts.get(0);
        }

        TableColumn<SearchResult, CheckBox> colCk = new TableColumn<>("选择");
        colCk.setCellValueFactory(cellData -> cellData.getValue().getCb().getCheckBox());

        TableColumn<SearchResult, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<SearchResult, String> colType = new TableColumn<>("类型");
        colType.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("type") {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SearchResult, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getType().getValue());
            }
        });
        TableColumn<SearchResult, String> colTitle = new TableColumn<>("标题");
        colTitle.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("title") {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SearchResult, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getTitle());
            }
        });
        TableColumn<SearchResult, String> colContent = new TableColumn<>("内容");
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        TableColumn<SearchResult, String> colLink = new TableColumn<>("链接");
        colLink.setCellValueFactory(new PropertyValueFactory<>("url"));
        colLink.setMinWidth(100);
        colLink.setCellFactory(param -> {
            TextFieldTableCell<SearchResult, String> cell = new TextFieldTableCell<>();
            cell.setOnMouseClicked((MouseEvent t) -> {
                if (t.getClickCount() == 2) {
                    try {
                        if (t.getTarget() instanceof TextFieldTableCell) {
                            URI uri = new URI(((TextFieldTableCell) t.getTarget()).getText());
                            Desktop.getDesktop().browse(uri);
                        } else {
                            URI uri = new URI(((LabeledText) t.getTarget()).getText());
                            Desktop.getDesktop().browse(uri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell;
        });

        tableView.getColumns().addAll(colCk, colId, colType, colTitle, colContent, colLink);
        term.setCreatedFrom(LocalDate.now().plusDays(-1).toDate());
        term.setPage(1);
        term.setSize(20);
        term.setMemberId(AppContext.getMemberId());
        term.addSort("created", Term.Sort.DESC);
        loadData();
    }

    private WebDriver driver;

    public void execSearch() {
        if (wbAccount == null) {
            lbTip.setText("微博帐户未设置");
            return;
        }
        if (StringUtil.isEmpty(txWord.getText())) {
            lbTip.setText("请输入关键词");
            return;
        }
        if (chbType.getSelectionModel().getSelectedItem() == null) {
            lbTip.setText("请选择链接类型");
            return;
        }
        try {
            String word = encode(txWord.getText(), "UTF-8");
            word = encode(word, "UTF-8");
            final String key = word;
            if (SearchController.this.driver == null) {
                driver = AppContext.getDriver(wbAccount.getUsername());
                if (driver == null) {
                    driver = SearchController.this.chromeBrowserService.getDriver(wbAccount);
                    AppContext.putDriver(wbAccount.getUsername(), driver);
                }
            }
            TaskContext context = new TaskContext(wbAccount, driver, 10);
            new ActionTask(context) {
                @Override
                public void run() {

                    this.context.setDriver(driver);
                    List<SearchResult> resultList = new ArrayList<>();
                    if (SearchType.综合.equals(chbType.getSelectionModel().getSelectedItem())) {
                        this.context.getDriver().get("http://s.weibo.com/weibo/" + key + "&Refer=focus_STopic_box");
                        List<WebElement> list = driver.findElements(By.cssSelector(".search_feed .WB_cardwrap"));
                        for (WebElement element : list) {
                            try {
                                WebElement face = element.findElement(By.cssSelector(".WB_feed_detail .feed_content a.W_texta.W_fb"));
                                String href = face.getAttribute("href");
                                String title = face.getAttribute("title");
                                WebElement content = element.findElement(By.cssSelector(".WB_feed_detail .feed_content .comment_txt"));
                                SearchResult result = new SearchResult();
                                result.setCreated(new Date());
                                result.setMemberId(AppContext.getMemberId());
                                result.setTitle(title);
                                result.setContent(content.getText().length() > 128 ? content.getText().substring(0, 128) : content.getText());
                                result.setType(SearchType.找人);
                                try {
                                    WebElement urlEl = element.findElement(By.cssSelector(".WB_feed_detail .content .feed_from a:nth-child(1)"));
                                    href = urlEl.getAttribute("href");
                                    result.setType(SearchType.综合);
                                } catch (Exception ignore) {
                                }
                                result.setUrl(href);
                                resultList.add(result);
                            } catch (Exception ignore) {

                            }
                        }
                    } else if (SearchType.找人.equals(chbType.getSelectionModel().getSelectedItem())) {
                        this.context.getDriver().get("http://s.weibo.com/user/" + key + "&Refer=weibo_user");
                        int totalPage = driver.findElements(By.cssSelector(".W_pages .list .W_scroll ul li")).size();
                        Tag tag = new Tag();
                        tag.setWord(txWord.getText());
                        fansService.saveTag(tag);
                        int page = 1;
                        List<WbMember> vs = new ArrayList<>(totalPage * 20);
                        while (true) {
                            List<WebElement> elements;
                            do {
                                //TODO: 搜索结果为空 会出现死循环
                                ThreadUtil.sleep(500);
                                elements = driver.findElements(By.cssSelector(".pl_personlist .list_person"));
                            } while (elements.isEmpty());
                            List<WbMember> wbMembers = new ArrayList<>(elements.size());
                            for (WebElement element : elements) {
                                SearchResult result = new SearchResult();
                                result.setCreated(new Date());
                                result.setMemberId(AppContext.getMemberId());
                                result.setType(SearchType.找人);
                                WebElement he = element.findElement(By.cssSelector(".person_pic a img"));
                                WbMember wbMember = new WbMember();
                                wbMember.setHeadImg(he.getAttribute("src"));
                                wbMember.setId(Long.parseLong(he.getAttribute("uid")));
                                WebElement de_name = element.findElement(By.cssSelector(".person_detail .person_name a"));
                                wbMember.setNick(de_name.getAttribute("title"));
                                result.setTitle(wbMember.getNick());
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
                                    result.setContent(wbMember.getCard().length() > 128 ? wbMember.getCard().substring(0, 128) : wbMember.getCard());
                                } catch (Throwable ignore) {
                                    wbMember.setCard("");
                                    result.setContent("");
                                }
                                result.setUrl(wbMember.getHome());
                                WebElement fansEl = element.findElement(By.cssSelector(".person_detail .person_num span:nth-child(2) a"));
                                String fansText = fansEl.getText();
                                if (fansText.endsWith("万")) {
                                    wbMember.setFansNum(Integer.parseInt(fansText.replace("万", "")) * 10000);
                                } else {
                                    wbMember.setFansNum(Integer.parseInt(fansText));
                                }
                                wbMember.setBigV(wbMember.getFansNum() > 10000);
                                wbMembers.add(wbMember);
                                resultList.add(result);
                            }
                            fansService.saveWbMembers(AppContext.getMemberId(), wbMembers);
                            vs.addAll(wbMembers);
                            if (page >= totalPage) {
                                break;
                            }
                            nextPage(driver, ++page);
                        }
                    }
                    if (!resultList.isEmpty()) {
                        searchResultDao.saves(resultList);
                        loadData();
                    }
                }
            }.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectAll() {
        for (SearchResult result : tableView.getItems()) {
            result.getCb().setSelected(true);
        }
    }

    public void dealSelected() {
        List<SearchResult> results = new ArrayList<>();
        for (SearchResult result : tableView.getItems()) {
            if (result.getCb().isSelected()) {
                results.add(result);
            }
        }
        AppContext.showMarketTab();
        AppContext.getMarketController().setSearchResults(results);
    }

    public void refresh() {
    }

    private void loadData() {
        List<SearchResult> resultList = searchResultDao.findPaging(term);
        ObservableList<SearchResult> tasks = observableArrayList(resultList);
        tableView.setItems(tasks);
        tableView.refresh();
        lbTotal.setText("总记录数: " + resultList.size());
    }

    private void nextPage(WebDriver driver, int page) {
        JavascriptExecutor je = (JavascriptExecutor) driver;
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

}
