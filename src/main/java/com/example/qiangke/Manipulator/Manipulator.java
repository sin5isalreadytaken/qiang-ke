package com.example.qiangke.Manipulator;

import com.example.qiangke.util.Dama2;
import com.example.qiangke.util.MailUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by wenxiangzhou214164 on 2017/9/5
 */
public class Manipulator {
    protected Logger logger = LogManager.getLogger(getClass());

    protected WebDriver driver = null;

    protected void tuike(List<WebElement> yixuanTds) {
        logger.info("[tuike] 开始退课");
        waitForElement(yixuanTds.get(0), By.cssSelector("input")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitForElement(By.cssSelector("button.btn.btn-warning")).click();
        driver.switchTo().alert().accept();
        logger.info("[tuike] 退课成功");
        driver.navigate().refresh();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void xuanke(List<WebElement> tds) {
        String name = tds.get(1).getText();
        String keyuliang = tds.get(7).getText();
        logger.info("[xuanke] keyuliang:{}", keyuliang);
        executeJS("window.scrollTo(0, document.body.scrollHeight)");
        if (!"0".equals(keyuliang)) {
            MailUtil.sendMail(name);
            //选课
            logger.info("[xuanke] 开始选课");
            waitForElement(tds.get(0), By.cssSelector("input")).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebElement img = waitForElements(By.cssSelector("img.codeimg")).get(0);
            byte[] by = new byte[0];
            try {
                by = captureElement(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte2image(by, "D:\\workspace\\sohu\\code.png");
            String code = Dama2.getRandomCode(by, 42).toLowerCase();
            logger.info("[xuanke] code:{}", code);
            WebElement codeInput = waitForElements(By.cssSelector("input[name=\"checkcode\"]")).get(0);
            codeInput.clear();
            codeInput.sendKeys(code);
            waitForElements(By.cssSelector("button.btn.btn-primary")).get(0).click();
            WebElement error = waitForElement(By.cssSelector("div.alert.alert-block.alert-error.fade.in"), 1000);
            if (error != null) {
                String errorMsg = waitForElement(error, By.cssSelector("a")).getText();
                logger.info("[xuanke] 选课失败, error:{}", errorMsg);
                return;
            }
            logger.info("[xuanke] 选课成功");
            driver.navigate().refresh();
        }
    }

    protected WebDriver makeDriver() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(true);
        profile.setPreference("intl.charset.default", "UTF-8");
        String path = QiangKeManipulator.class.getResource("/geckodriver.exe").getPath();
        System.setProperty("webdriver.gecko.driver", path);
        WebDriver webDriver = new FirefoxDriver(profile);
        return webDriver;
    }

    protected WebElement waitForElement(WebElement we, By by) {
        return waitForElement(we, by, 10 * 1000);
    }

    protected WebElement waitForElement(By by) {
        return waitForElement(by, 10 * 1000);
    }

    protected WebElement waitForElement(By by, long lTimeout) {
        long lStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - lStart < lTimeout) {
            try {
                WebElement pElemenet = driver.findElement(by);
                if (null != pElemenet) {
                    return pElemenet;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        logger.info("[waitForElement] waitForElement {} timeout.", by.toString());
        return null;
    }

    protected WebElement waitForElement(WebElement we, By by, long lTimeout) {
        long lStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - lStart < lTimeout) {
            try {
                WebElement pElemenet = we.findElement(by);
                if (null != pElemenet) {
                    return pElemenet;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        logger.info("[waitForElement] waitForElement {} timeout.", by.toString());
        return null;
    }

    protected List<WebElement> waitForElements(WebElement we, By by) {
        return waitForElements(we, by, 10 * 1000);
    }

    protected List<WebElement> waitForElements(WebElement we, By by, long lTimeout) {
        long lStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - lStart < lTimeout) {
            try {
                List<WebElement> pElemenets = we.findElements(by);
                if (null != pElemenets) {
                    return pElemenets;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        logger.info("[waitForElements] waitForElements {} timeout.", by.toString());
        return null;
    }

    protected List<WebElement> waitForElements(By by, long lTimeout) {
        long lStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - lStart < lTimeout) {
            try {
                List<WebElement> pElemenets = driver.findElements(by);
                if (null != pElemenets) {
                    return pElemenets;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        logger.info("[waitForElements] waitForElements {} timeout.", by.toString());
        return null;
    }

    protected List<WebElement> waitForElements(By by) {
        return waitForElements(by, 10 * 1000);
    }

    protected boolean checkUrl(String url) {
        try {
            if (url.equals(driver.getCurrentUrl())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取指定元素的截图byte[]
     */
    protected byte[] captureElement(WebElement we) throws Exception {
        BufferedImage bi = getElementImage(we);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", bos);
        return bos.toByteArray();
    }

    /**
     * 获取指定元素的截图
     */
    protected BufferedImage getElementImage(WebElement we) {
        Point location = we.getLocation();
        Dimension size = we.getSize();
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
        } catch (IOException e) {
            logger.info("read img failed");
        }
        return originalImage.getSubimage(location.getX(), 360, size.getWidth(), size.getHeight());
    }

    /**
     * 根据地址获得数据的字节流
     * @param strUrl 网络连接地址
     *
     */
    protected static byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            return readInputStream(inStream);//得到图片的二进制数据
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     *
     */
    protected static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    //byte数组到图片
    public void byte2image(byte[] data,String path){
        if(data.length<3||path.equals("")) return;
        try{
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch(Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

    protected void executeJS(String js) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript(js);
    }
}
