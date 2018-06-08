package cn.huwhy.weibo.robot.task;

import cn.huwhy.common.util.RandomUtil;
import cn.huwhy.common.util.ThreadUtil;
import cn.huwhy.weibo.robot.AppContext;
import cn.huwhy.weibo.robot.action.ActionUtil;
import cn.huwhy.weibo.robot.model.SearchResult;
import cn.huwhy.weibo.robot.model.WbAccount;
import cn.huwhy.weibo.robot.model.WbMember;
import cn.huwhy.weibo.robot.model.common.SearchType;
import cn.huwhy.weibo.robot.service.ChromeBrowserService;
import cn.huwhy.weibo.robot.service.FansService;
import cn.huwhy.weibo.robot.service.WbAccountService;
import org.openqa.selenium.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class GroupActionTask implements Serializable {

    private TaskGroupContent context;
    private ArrayBlockingQueue<SearchResult> queue;
    private ChromeBrowserService chromeBrowserService;
    private FansService fansService;
    private WbAccountService accountService;
    private boolean finished;
    private boolean nextAccount = true;
    private boolean nextResult = true;

    public GroupActionTask(TaskGroupContent context,
                           ArrayBlockingQueue<SearchResult> queue,
                           ChromeBrowserService chromeBrowserService,
                           WbAccountService accountService,
                           FansService fansService) {
        this.context = context;
        this.queue = queue;
        this.chromeBrowserService = chromeBrowserService;
        this.fansService = fansService;
        this.accountService = accountService;
    }

    public void run() {
        WbAccount account;
        if (context.isNextAccount()) {
            account = context.nextAccount();
            if (account == null) {
                context.setFinished(true);
                return;
            }
            //切换帐号
            try {
                if (context.getDriver().findElements(By.className("login_btn")).size() == 0) {
                    WebElement element = context.getDriver().findElement(By.cssSelector(".gn_position a[node-type=account]"));
                    ActionUtil.moveToEl(context.getDriver(), element);
                    ThreadUtil.sleepSeconds(2);
                    WebElement quitElement = context.getDriver().findElement(By.cssSelector("a[suda-data='key=account_setup&value=quit']"));
                    quitElement.click();
                    ThreadUtil.sleepSeconds(2);
                }
            } catch (Exception ignore) {
                context.getDriver().get("https://weibo.com/");
            }
            chromeBrowserService.loginWb(account, context.getDriver());
        } else {
            account = context.currentAccount();
        }
        SearchResult result;
        if (context.isNextResult()) {
            result = queue.poll();
            if (result == null) {
                context.setFinished(true);
                return;
            }
            context.setResult(result);
        } else {
            result = context.getResult();
        }
        if (context.isNextAccount() || context.isNextResult()) {
            context.getDriver().get(result.getUrl());
        }
        if (result.getType().equals(SearchType.找人)) {
            execSendSmsToFollow(context, account);
        } else {
            sendSmsToCommentFans(context, account);
        }

        if (context.getAccountCounter() == account.getMsgLimit()) {
            account.setLastSendTime(new Date());
            accountService.save(account);
            context.setNextAccount(true);
        }
        ThreadUtil.sleepSeconds(10);

        if (context.getWbMembers().size() > 0) {
            fansService.saveWbMembers(AppContext.getMemberId(), context.getWbMembers());
            context.clearWbMembers();
        }
    }

    /**
     * 对粉丝列表发私信(只能抓取前5页大概100条数据)
     *
     * @param context 上下文
     */
    private void execSendSmsToFollow(TaskGroupContent context, WbAccount account) {
        WebDriver driver = context.getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        if (context.isNextResult() || context.isNextAccount()) {
            try {
                WebElement counterEL = driver.findElement(By.cssSelector(".PCD_counter"));
                List<WebElement> counterList = counterEL.findElements(By.cssSelector(".S_line1"));
                try {
                    String link = counterList.get(1).findElement(By.cssSelector(".t_link")).getAttribute("href");
                    if (context.getPage() == 1) {
                        driver.get(link);
                    } else {
                        String url = link.substring(0, link.indexOf('?')) + "page=" + context.getPage() + "&" + link.substring(link.indexOf('?') + 1);
                        driver.get(url);
                    }
                } catch (Throwable err) {
                    String url = context.getResult().getUrl();
                    String link = url.substring(0, url.indexOf('?')) + "/fans";
                    driver.get(link);
                }
            } catch (NoSuchElementException err) {
                driver.navigate().refresh();
            } finally {
                context.setNextAccount(false);
            }
        }
        context.setNextResult(false);
        while (true) {
            List<WebElement> fansList = driver.findElements(By.cssSelector(".follow_box .follow_list .follow_item:nth-child(n+" + context.getResultCounter() + ")"));
                if (fansList.size() == 0) {
                context.setNextResult(true);
                break;
            }
            ThreadUtil.sleepSeconds(1);
            for (WebElement fans : fansList) {
                WebElement headEl = fans.findElement(By.cssSelector(".mod_pic a img"));
                String ac = headEl.getAttribute("usercard");
                if (!context.getSet().add(ac)) {
                    continue;
                }
                context.incrCounter();
                while (true) {
                    try {
                        //沉淀粉丝
                        WbMember wbMember = new WbMember();
                        WebElement we = fans.findElement(By.cssSelector(".mod_pic a"));
                        wbMember.setHeadImg(headEl.getAttribute("src"));
                        ac = ac.replace("id=", "");
                        if (ac.indexOf('&') != -1) {
                            ac = ac.substring(0, ac.indexOf("&"));
                        }
                        wbMember.setId(Long.parseLong(ac));
                        wbMember.setNick(we.getAttribute("title"));
                        wbMember.setHome(we.getAttribute("href"));

                        String sexClass = fans.findElement(By.cssSelector(".mod_info .info_name a:nth-child(2)")).getAttribute("class");
                        wbMember.setSex(sexClass.contains("icon_female") ? "female" : "male");

                        WebElement de_addr = fans.findElement(By.cssSelector(".mod_info .info_add span"));
                        wbMember.setAddr(de_addr.getText());
                        wbMember.setTagIds("");
                        wbMember.setvTagIds("");

                        try {
                            WebElement de_card = fans.findElement(By.cssSelector(".mod_info .info_intro span"));
                            wbMember.setCard(de_card.getText());
                        } catch (Throwable ignore) {
                            wbMember.setCard("");
                        }
                        WebElement fansEl = fans.findElement(By.cssSelector(".mod_info .info_connect span:nth-child(2) em a"));
                        String fansText = fansEl.getText();
                        if (fansText.endsWith("万")) {
                            wbMember.setFansNum(Integer.parseInt(fansText.replace("万", "")) * 10000);
                        } else {
                            wbMember.setFansNum(Integer.parseInt(fansText));
                        }
                        wbMember.setBigV(wbMember.getFansNum() > 10000);
                        context.getWbMembers().add(wbMember);
                        //沉淀粉丝结束

                        WebElement e = fans.findElement(By.cssSelector(".opt_box a[action-type=opt_box_more]"));
                        ActionUtil.moveToEl(driver, e);
                        ThreadUtil.sleepSeconds(1);
                        WebElement el2 = fans.findElement(By.cssSelector(".layer_menu_list a[action-type='webim.conversation']"));
                        je.executeScript("arguments[0].scrollIntoView(false);", el2);
                        ActionUtil.moveToEl(driver, el2);
                        el2.click();
                        ThreadUtil.sleepSeconds(1);
                        WebElement wechatEl = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
                        ActionUtil.moveToEl(driver, wechatEl);
                        ThreadUtil.sleepSeconds(2);
                        System.out.println(wechatEl.getText());
                        WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
                        input.sendKeys(context.getMsg());
                        ThreadUtil.sleep(100);
                        //TODO:
                        input.sendKeys(Keys.ENTER);
                        ThreadUtil.sleep(100);
                        break;
                    } catch (Exception nve) {
                        ThreadUtil.sleepSeconds(1);
                    }
                }
                List<WebElement> yzmList = driver.findElements(By.cssSelector(".yzm_input"));
                ThreadUtil.sleepSeconds(1);
                if (yzmList.size() > 0) {
                    account.setMsgLimit(context.getAccountCounter());
                    return;
                }
                ActionUtil.click(driver, ".webim_chat_window .chat_head .W_ficon.ficon_close");
                if (context.getAccountCounter() % 5 == 0 || context.getAccountCounter() == account.getMsgLimit()) {
                    return;
                } else {
                    ThreadUtil.sleepSeconds(RandomUtil.randomInt(5));
                }
            }
            context.incrPage();
            if (context.getPage() > 5) {
                context.setNextResult(true);
                break;
            }
            WebElement next = driver.findElement(By.cssSelector(".W_pages .next"));
            if (!next.getAttribute("class").contains("page_dis")) {
                ActionUtil.moveToEl(driver, next);
                next.click();
                waitPage(driver, context.getPage());
            } else {
                context.setNextResult(true);
                break;
            }
        }
    }

    private void waitPage(WebDriver driver, int page) {
        do {
            try {
                WebElement pageEl = driver.findElement(By.cssSelector(".W_pages .page.S_bg1"));
                if (Integer.parseInt(pageEl.getText()) == page) {
                    break;
                }
                ThreadUtil.sleep(300);
            } catch (Throwable e) {
                ThreadUtil.sleep(500);
            }
        } while (true);
    }

    /**
     * 抓取我的微博下评论的粉丝
     *
     * @param context 上下文
     */
    private void sendSmsToCommentFans(TaskGroupContent context, WbAccount account) {
        WebDriver driver = context.getDriver();
        JavascriptExecutor je = (JavascriptExecutor) driver;
        context.setNextResult(false);
        try {
            while (true) {
                List<WebElement> list;
                ThreadUtil.sleepSeconds(2);
                int mnt = 0;
                while (true) {
                    mnt++;
                    list = driver.findElements(By.cssSelector(".repeat_list .list_ul .list_li:nth-child(n+" + context.getResultCounter() + ")"));
                    if (list.size() > 0 || mnt > 3) break;
                    ThreadUtil.sleep(500);
                }
                for (WebElement comment : list) {
                    WebElement face = comment.findElement(By.cssSelector(".WB_text a"));
                    if (!context.getSet().add(face.getAttribute("usercard"))) {
                        continue;
                    }
                    context.incrCounter();
                    String href = face.getAttribute("href");
                    if (!driver.getCurrentUrl().startsWith(href)) {
                        while (true) {
                            try {
                                je.executeScript("arguments[0].scrollIntoView(false);", face);
                                ActionUtil.moveToEl(driver, face);
                                ThreadUtil.sleep(500);
                                WebElement fcEl = driver.findElement(By.cssSelector(".W_layer_pop .layer_personcard .c_btnbox a:nth-child(3)"));
                                fcEl.click();
                                WebElement wechatEl = driver.findElement(By.cssSelector(".webim_chat_window .chat_head .chat_title a[node-type=_chatUserName]"));
                                ActionUtil.moveToEl(driver, wechatEl);
                                ThreadUtil.sleepSeconds(2);
                                System.out.println(wechatEl.getText());
                                WebElement input = driver.findElement(By.cssSelector(".webim_chat_window .sendbox_area .W_input"));
                                input.sendKeys(context.getMsg());
                                ThreadUtil.sleep(100);
                                input.sendKeys(Keys.ENTER);
                                ThreadUtil.sleep(100);
                                ActionUtil.moveToEl(driver, face);
                                break;
                            } catch (Exception e) {
                                ThreadUtil.sleep(500);
                            }
                        }
                        while (true) {
                            try {
                                driver.findElement(By.cssSelector(".yzm_input"));
                                ThreadUtil.sleepSeconds(3);
                            } catch (Throwable thr) {
                                break;
                            }
                        }
                        ThreadUtil.sleepSeconds(1);
                        ActionUtil.click(driver, ".webim_chat_window .chat_head .W_ficon.ficon_close");
                    }
                    if (context.getAccountCounter() % 5 == 0 || context.getAccountCounter() == account.getMsgLimit()) {
                        return;
                    } else {
                        ThreadUtil.sleepSeconds(RandomUtil.randomInt(5));
                    }
                }

                try {
                    WebElement next = driver.findElement(By.cssSelector("a[action-type='click_more_comment']"));
                    next.click();
                } catch (Exception e) {
                    list = driver.findElements(By.cssSelector(".repeat_list .list_ul .list_li:nth-child(n+" + (context.getResultCounter() + 1) + ")"));
                    if (list.size() == 0) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isNextAccount() {
        return nextAccount;
    }

    public void setNextAccount(boolean nextAccount) {
        this.nextAccount = nextAccount;
    }

    public boolean isNextResult() {
        return nextResult;
    }

    public void setNextResult(boolean nextResult) {
        this.nextResult = nextResult;
    }

    public TaskGroupContent getContext() {
        return context;
    }
}
