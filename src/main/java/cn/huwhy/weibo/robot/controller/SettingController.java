package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.Base64;
import cn.huwhy.common.util.RandomUtil;
import cn.huwhy.common.util.StringUtil;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingController extends BaseController implements Initializable {
    private String code;
    @FXML
    private Label lbCode, lbPwd, lbPwd2, lbCode2;
    @FXML
    private Text txUsername, actionTarget;
    @FXML
    private TextField txPwd, txPwd2, txCode;
    @FXML
    private Button btnChg, btnSave;
    private MemberService memberService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        memberService = SpringContentUtil.getBean(MemberService.class);
        code = RandomUtil.getRandomNum(4);
        refreshCode();
        Member member = AppContext.getMember();
        txUsername.setText(member.getName());
        lbPwd.setVisible(false);
        txPwd.setVisible(false);
        lbPwd2.setVisible(false);
        txPwd2.setVisible(false);
        lbCode2.setVisible(false);
        txCode.setVisible(false);
        lbCode.setVisible(false);
        btnChg.setVisible(true);
        btnSave.setVisible(false);

    }

    public void refreshCode() {
        code = RandomUtil.getRandomNum(4);
        lbCode.setText("验证码 " + code + " 刷新");
    }

    @FXML
    public void btnChgClick() {
        lbPwd.setVisible(true);
        txPwd.setVisible(true);
        lbPwd2.setVisible(true);
        txPwd2.setVisible(true);
        lbCode2.setVisible(true);
        txCode.setVisible(true);
        lbCode.setVisible(true);
        btnSave.setVisible(true);
        btnChg.setVisible(false);
    }

    @FXML
    public void btnSaveClick() {
        Member member = AppContext.getMember();
        String text = txPwd.getText();
        String pwd = new String(Base64.encode(text.getBytes()));
        if (StringUtil.isEmpty(txPwd2.getText()) || txPwd2.getText().length() < 5) {
            actionTarget.setText("新密码请输入5位及以上字符");
        } else if (!member.getPassword().equals(pwd)) {
            actionTarget.setText("原密码输入错误");
        } else if (!txCode.getText().equals(this.code)) {
            actionTarget.setText("验证码错误");
        } else {
            member.setPassword(new String(Base64.encode(txPwd2.getText().getBytes())));
            this.memberService.save(member);
            actionTarget.setText("修改成功");
        }
    }
}
