package cn.huwhy.weibo.robot.action;

import cn.huwhy.weibo.robot.model.MemberConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentSettingAction {

    private static Logger logger = LoggerFactory.getLogger(CommentSettingAction.class);

    public MemberConfig memberConfig;
    private WebDriver driver;
    private volatile boolean running = false;
    public CommentSettingAction() {

    }

    public void run() {
        if (running) return;
        running = true;
        try {
            WebElement el = driver.findElement(By.cssSelector(".gn_set .gn_set_list:nth-child(2)"));
            ActionUtil.moveToEl(driver, ".gn_set .gn_set_list a[node-type=account] em");
            while(true) {
                try {
                    ActionUtil.click(el, ".gn_topmenulist_set ul li:nth-child(1)");
                    ActionUtil.click(driver, ".ico_message");
                    ActionUtil.click(driver, "#pl_message_comment .set_opt");
                    List<WebElement> list = driver.findElements(By.cssSelector("#pl_message_comment input[name=comment]"));
                    for (WebElement e : list) {
                        if (e.getAttribute("value").equals("1")) {
                            e.click();
                        }
                    }
                    ActionUtil.click(driver, "#pl_message_comment .W_btn_a[action-type=save]");
                    break;
                } catch (Throwable ignore){}
            }
        } finally {
            running = false;
        }
    }

    public void setMemberConfig(MemberConfig memberConfig) {
        this.memberConfig = memberConfig;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
