package com.testfm.driver;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

/**
 * DriverManager handles WebDriver lifecycle for Edge browser.
 * Points to the local msedgedriver.exe in the /drivers folder.
 */
public class DriverManager {

    private static WebDriver driver;

    private DriverManager() {
        // Utility class - no instantiation
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            initDriver();
        }
        return driver;
    }

    private static void initDriver() {
        // Point to the local Edge driver
        String driverPath = System.getProperty("user.dir") + "\\drivers\\msedgedriver.exe";
        System.setProperty("webdriver.edge.driver", driverPath);

        EdgeOptions options = new EdgeOptions();

        // Finish navigation at DOMContentLoaded — avoids renderer timeouts on heavy homepages.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

           // ✅ SSL BYPASS (THIS IS THE FIX)
          options.setAcceptInsecureCerts(true);
          options.addArguments("--ignore-certificate-errors");

        // Uncomment the line below to run tests headlessly (no visible browser window)
        // options.addArguments("--headless");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new EdgeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("[DriverManager] Edge WebDriver initialized.");
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("[DriverManager] Edge WebDriver quit.");
        }
    }
}
