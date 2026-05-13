package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class YouthFriendlyPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By youthFriendlyButton =
        By.xpath("//*[@id='User-Multiple-Male-Female--Streamline-Ultimate']");
    private final By familyRoasterButton =
        By.xpath("//*[@id='tlMainContent']/div/div[1]/div[2]/div/p/a");
    private final By familyMemberHeading =
        By.xpath("//*[@id='block-teamleaders-content']/div[1]/h1");

    public YouthFriendlyPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickYouthFriendlyButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(youthFriendlyButton));
        scrollIntoView(button);
        button.click();
        System.out.println("[YouthFriendlyPage] Clicked Youth Friendly button.");
    }

    public void clickFamilyRoasterButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(familyRoasterButton));
        scrollIntoView(button);
        button.click();
        System.out.println("[YouthFriendlyPage] Clicked Family Roaster button.");
    }

    public String getFamilyMemberHeadingText() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(familyMemberHeading));
        return heading.getText().trim();
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}
