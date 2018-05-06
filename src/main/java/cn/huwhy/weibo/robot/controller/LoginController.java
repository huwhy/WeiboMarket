package cn.huwhy.weibo.robot.controller;

import cn.huwhy.common.util.Base64;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.service.MemberService;
import cn.huwhy.weibo.robot.util.SpringContentUtil;
import cn.huwhy.weibo.robot.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.Test;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends BaseController implements Initializable{

    @FXML
    private Text actionTarget;
    @FXML
    private TextField txUsername;
    @FXML
    private PasswordField txPassword;

    private MemberService memberService;

    @FXML
    public void handleSubmitButtonAction(ActionEvent e) {
        actionTarget.setText("Sign in button pressed");
        String text = txPassword.getText();
        if (txUsername.getText().equals("") || text.equals("")) {
            actionTarget.setText("用户密码错误");
        } else {
            Member member = memberService.getByName(txUsername.getText());
            String pwd = new String(Base64.encode(text.getBytes()));
            if (member != null && member.getPassword().equals(pwd)) {
                member.setConfig(memberService.getConfig(member.getId()));
                AppContext.showMain(member);
            } else {
                actionTarget.setText("用户密码错误");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.memberService = SpringContentUtil.getBean(MemberService.class);
    }
}
