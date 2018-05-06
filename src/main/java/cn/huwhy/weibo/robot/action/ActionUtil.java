package cn.huwhy.weibo.robot.action;

import cn.huwhy.common.util.ThreadUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ActionUtil {

    public static void click(WebDriver driver, String cssSelector) {
        int i = 0;
        while (true) {
            i++;
            try {
                driver.findElement(By.cssSelector(cssSelector)).click();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i >= 7) {
                break;
            }
            ThreadUtil.sleepSeconds(1);
        }
    }

    public static void click(WebElement element, String cssSelector) {
        int i = 0;
        while (true) {
            i++;
            try {
                element.findElement(By.cssSelector(cssSelector)).click();
                break;
            }catch(Exception e) {e.printStackTrace();}
            if (i >= 7) {
                break;
            }
            ThreadUtil.sleepSeconds(1);
        }
    }

    public static void moveToEl(WebDriver driver, String cssSelector) {
        int i = 0;
        while (true) {
            i++;
            try {
                new Actions(driver).moveToElement(driver.findElement(By.cssSelector(cssSelector))).perform();
                break;
            } catch(Exception e) {e.printStackTrace();}
            if (i >= 7) {
                break;
            }
            ThreadUtil.sleepSeconds(1);
        }
    }

    public static void moveToEl(WebDriver driver, WebElement element) {
        int i = 0;
        while (true) {
            i++;
            try {
                new Actions(driver).moveToElement(element).perform();
                break;
            } catch (Exception e) {e.printStackTrace();}
            if (i >= 7) {
                break;
            }
            ThreadUtil.sleepSeconds(1);
        }
    }
}
