package cn.huwhy.weibo.robot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import static cn.huwhy.weibo.robot.util.SystemType.WINDOWS;

public class ResourcesUtil {

    public final static SystemType SYSTEM_TYPE;

    private static Logger logger = LoggerFactory.getLogger(ResourcesUtil.class);

    static {
        String osName = System.getProperties().getProperty("os.name").toUpperCase();
        if (osName.contains("WINDOWS")) {
            SYSTEM_TYPE = WINDOWS;
        } else if (osName.contains("Mac")) {
            SYSTEM_TYPE = SystemType.MAC;
        } else {
            SYSTEM_TYPE = SystemType.LINUX;
        }

    }

    public static File getChromeDrivers() {
        try {
            String conf = System.getProperty("user.dir");
            logger.info("app_root: {}", conf);
            String driverName = conf + (SYSTEM_TYPE == WINDOWS ? "/drivers/chromedriver.exe" : "/drivers/chromedriver");
            return new File(driverName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getImage(String imageName) {
        try {
            return ResourcesUtil.class.getClassLoader().getResource("images/" + imageName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

