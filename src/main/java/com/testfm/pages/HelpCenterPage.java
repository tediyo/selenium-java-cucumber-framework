package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for the Help Center flow reached from Youth Friendly.
 */
public class HelpCenterPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By helpCenterButton =
        By.xpath("//*[@id=\"family-intro\"]/a[2]");
    private final By helpCenterHeading =
        By.xpath("//*[@id=\"block-teamleaders-content\"]/article/div/div/section/hgroup/h1");

    public HelpCenterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickHelpCenterButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(helpCenterButton));
        scrollIntoView(button);
        button.click();
        System.out.println("[HelpCenterPage] Clicked Help Center button.");
    }

    public String getHelpCenterHeadingText() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(helpCenterHeading));
        return heading.getText().trim();
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}

