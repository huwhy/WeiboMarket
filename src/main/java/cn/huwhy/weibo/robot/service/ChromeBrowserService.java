package cn.huwhy.weibo.robot.service;

import cn.huwhy.common.util.StringUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.weibo.robot.model.Member;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.util.ResourcesUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChromeBrowserService implements DisposableBean, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private DriverService service;
    private WebDriver driver;

    @Override
    public void destroy() throws Exception {
        service.stop();
        driver.quit();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File driverFile = ResourcesUtil.getChromeDrivers();
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(driverFile)
                .usingAnyFreePort()
                .build();
        service.start();
    }

    private void initDriver() {
        if (this.driver != null) {
            try {
                this.driver.quit();
            } catch (Throwable ignore) {
                logger.warn("", ignore);
            } finally {
                this.driver = null;
            }
        }
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--kiosk");//全屏
//        chromeOptions.addArguments("--start-maximized");
        this.driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome().merge(chromeOptions));
    }

    public void login(Member member) {
        this.initDriver();
        if (StringUtil.isNotEmpty(member.getWbName()) && StringUtil.isNotEmpty(member.getWbPassword())) {
            //得到浏览器的标题
            this.driver.get("https://weibo.com/");
            ThreadUtil.sleep(1000);
            int loginCnt = 0;
//            do {
//                logger.debug("login weibo ...");
//                loginCnt++;
//                try {
//                    WebElement loginname = driver.findElement(By.id("loginname"));
//                    WebElement password = driver.findElement(By.name("password"));
//                    loginname.sendKeys(member.getWbName());
//                    password.sendKeys(new String(Base64.decode(member.getWbPassword().getBytes())));
//                    WebElement login_btn = driver.findElement(By.className("login_btn"));
//                    login_btn.click();
//
//                    int cnt = 0;
//                    while (true) {
//                        cnt++;
//                        try {
//                            driver.findElement(By.className("login_btn"));
//                        } catch (Throwable e) {
//                            break;
//                        }
//                        if (cnt >= 3) {
//                            break;
//                        }
//                        ThreadUtil.sleepSeconds(1);
//                    }
//                    if (cnt < 3) {
//                        break;
//                    } else {
//                        this.driver.get("https://weibo.com/");
//                    }
//                } catch (Throwable err) {
//                    ThreadUtil.sleep(1000);
//                }
//                if (loginCnt >= 3) {
//                    break;
//                }
//            } while (true);
            logger.debug("login weibo end");
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    private static String uri = "https://weibo.com/comment/inbox?topnav=1&wvr=6&f=1";

    public WebDriver getDriver(WbAccount account) {
        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriver driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome().merge(chromeOptions));
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(screensize.width, screensize.height));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        if (StringUtil.isNotEmpty(account.getUsername()) && StringUtil.isNotEmpty(account.getPassword())) {
            //得到浏览器的标题
            driver.get("https://weibo.com/");
            ThreadUtil.sleep(1000);
            loginWb(account, driver);
            do {
                List<WebElement> list = driver.findElements(By.className("login_btn"));
                if (list.size() == 0) break;
//                ThreadUtil.sleepSeconds(3);
//                driver.get(uri);
//                ThreadUtil.sleepSeconds(3);
//                String uri = driver.getCurrentUrl();
//                if (uri.startsWith("https://weibo.com/login")) {
//                } else {
//                    ThreadUtil.sleepSeconds(3);
//                    break;
//                }
                ThreadUtil.sleepSeconds(3);
            } while (true);
            logger.debug("login weibo end");
        }

        return driver;
    }

    public void loginWb(WbAccount account, WebDriver driver) {
        int loginCnt = 0;
        do {
            logger.debug("login weibo ...");
            loginCnt++;
            try {
                WebElement loginname = driver.findElement(By.id("loginname"));
                loginname.clear();
                WebElement password = driver.findElement(By.name("password"));
                password.clear();
                loginname.sendKeys(account.getUsername());
                password.sendKeys(account.getPassword());
//                    password.sendKeys(new String(Base64.decode(account.getPassword().getBytes())));
                WebElement login_btn = driver.findElement(By.className("login_btn"));
                login_btn.click();

                int cnt = 0;
                while (true) {
                    cnt++;
                    try {
                        driver.findElement(By.className("login_btn"));
                    } catch (Throwable e) {
                        break;
                    }
                    if (cnt >= 5) {
                        break;
                    }
                    ThreadUtil.sleepSeconds(1);
                }
                if (cnt < 5) {
                    break;
                } else {
                    driver.get("https://weibo.com/");
                }
            } catch (Throwable err) {
                ThreadUtil.sleep(1000);
            }
            if (loginCnt >= 7) {
                break;
            }
        } while (true);
    }

}
