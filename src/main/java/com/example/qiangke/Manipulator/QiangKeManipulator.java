package com.example.qiangke.Manipulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QiangKeManipulator extends Manipulator{
    private Logger logger = LogManager.getLogger(getClass());

    private boolean ready = false;
    private int retry = 10;

    private String url = "https://gsdb.bjtu.edu.cn/course_selection/select/";

    public void task() {
        getContent();
    }

    private boolean loginAndJump(boolean login) {
        if (login) {
            try {
                if (driver != null) {
                    driver.close();
                }
                if (null == driver) {
                    driver = makeDriver();
                    logger.info("[loginAndJump] open browser");
                }
                if (driver == null) {
                    logger.info("[loginAndJump] make driver failed");
                    return false;
                }
                driver.navigate().to("https://gsdb.bjtu.edu.cn/client/login/");
                List<WebElement> logins = waitForElements(By.cssSelector("input.input-small"));
                WebElement btn = waitForElement(By.cssSelector("button.btn.btn-small.btn-primary"));
                logins.get(0).clear();
                logins.get(1).clear();
                logins.get(0).sendKeys("");
                logins.get(1).sendKeys("");
                btn.click();
                Thread.sleep(1000);
                driver.navigate().to(url);
                return true;
            } catch (Exception e) {
                logger.info("login failed");
                return false;
            }
        }
        else {
            return loginAndJump();
        }
    }

    private boolean loginAndJump() {
        if (driver != null) {
            driver.close();
        }
        if (null == driver) {
            driver = makeDriver();
            logger.info("[loginAndJump] open browser");
        }
        if (driver == null) {
            logger.info("[loginAndJump] make driver failed");
            return false;
        }
        driver.navigate().to(url);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void refresh() {
        try {
            if (!checkUrl(url)) {
                driver.navigate().to(url);
            }
            else {
                driver.navigate().refresh();
            }
            logger.info("[refresh] refresh");
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.info("[refresh] open browser while refresh failed");
            driver = null;
            ready = false;
        }
    }

    private void getContent() {
        refresh();
        for (int i = 0; i < retry && (!ready || driver == null); i++) {
            ready = loginAndJump(true);
        }
        if (!ready) {
            logger.info("[getContent] login jump failed");
        }
        WebElement table = waitForElements(By.cssSelector("table.table.table-bordered.table-striped.table-condensed.selectable")).get(1);
        List<WebElement> trs = waitForElements(table, By.cssSelector("tr"));

        trs.forEach((tr) -> {
            List<WebElement> tds = waitForElements(tr, By.cssSelector("td"));
            if (!tds.isEmpty()) {
                String name = tds.get(1).getText();
                switch (name) {
                    case "城市交通分析与设计":
                        logger.info("[getContent] name:{}", name);
                        xuanke(tds);
                        break;
                    case "交通专业软件应用技术":
                        logger.info("[getContent] name:{}", name);
                        xuanke(tds);
                        break;
                    case "网络信息系统的分析与设计":
                        logger.info("[getContent] name:{}", name);
                        xuanke(tds);
                        break;
                    case "预测理论与方法":
                        logger.info("[getContent] name:{}", name);
                        xuanke(tds);
                        break;
                    case "现代控制工程":
                        logger.info("[getContent] name:{}", name );
                        xuanke(tds);
                        break;
                    default: break;
                }
            }
        });
    }
}
