package cn.huwhy.weibo.robot.controller;

import cn.huwhy.interfaces.Paging;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.model.MyFansTerm;
import cn.huwhy.weibo.robot.model.WordType;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class DataController extends BaseController implements Initializable {

    @FXML
    private Text txTotal, txRed, txBlack;
    @FXML
    private Text txTotal2, txRed2, txBlack2;
    @FXML
    private PieChart chartCount, chartCount2;

    private FansService fansService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fansService = SpringContentUtil.getBean(FansService.class);
        count();
    }

    @Override
    public void refresh() {
        count();
    }

    private void count() {
        MyFansTerm term = new MyFansTerm();
        term.setPage(1);
        term.setSize(1);
        term.setMemberId(AppContext.getMemberId());
        Paging paging = fansService.findMyFans(term);
        long totalNum, redNum, blackNum;
        totalNum = paging.getTotal();
        txTotal.setText("" + totalNum);

        term.setType(WordType.IRON);
        paging = fansService.findMyFans(term);
        redNum = paging.getTotal();
        double redRate = totalNum == 0 ? 0 : redNum * 100.0 / totalNum;
        txRed.setText(redNum + "");

        term.setType(WordType.BLACK);
        paging = fansService.findMyFans(term);
        blackNum = paging.getTotal();
        double blackRate = totalNum == 0 ? 0 : blackNum * 100.0 / totalNum;
        txBlack.setText(blackNum + "");
        double otherRate =  totalNum == 0 ? 0 : (100 - redRate - blackRate);

        ObservableList<PieChart.Data> pieChartData =
                observableArrayList(
                        new PieChart.Data(String.format("铁粉%.2f%%", redRate), redNum),
                        new PieChart.Data(String.format("黑粉%.2f%%", blackRate), blackNum),
                        new PieChart.Data(String.format("吃瓜群众%.2f%%", otherRate), totalNum - redNum - blackNum)
                );
        chartCount.setTitle("粉丝统计(总数:" + totalNum + ")");
        chartCount.setData(pieChartData);

        Member member = AppContext.getMember();
        txTotal2.setText("" + member.getCommentNum());
        txRed2.setText("" + member.getRedCommentNum());
        double red2Rate = member.getCommentNum() == 0 ? 0 : member.getRedCommentNum() * 100.0 / member.getCommentNum();
        txBlack2.setText("" + member.getBlackCommentNum());
        double black2Rate = member.getCommentNum() == 0 ? 0 : member.getBlackCommentNum() * 100.0 / member.getCommentNum();
        int other2 = member.getCommentNum() == 0 ? 0 : member.getCommentNum() - member.getRedCommentNum() - member.getBlackCommentNum();
        double other2Rate = member.getCommentNum() == 0 ? 0 : (100 - red2Rate - black2Rate);
        ObservableList<PieChart.Data> pieChartData2 =
                observableArrayList(
                        new PieChart.Data(String.format("铁粉评论%.2f%%", red2Rate), member.getRedCommentNum()),
                        new PieChart.Data(String.format("黑粉评论%.2f%%", black2Rate), member.getBlackCommentNum()),
                        new PieChart.Data(String.format("吃瓜群众评论%.2f%%", other2Rate), other2)
                );
        chartCount.setTitle("评论统计(总数:" + member.getCommentNum() + ")");
        chartCount2.setData(pieChartData2);
    }
}
