package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProjectResourcePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By resourcesButtonSvg =
        By.xpath("//*[@id='block-teamleaders-main-menu']/div/aside/ul/li[6]/a/span[1]/svg");
    private final By resourcesButtonLink =
        By.xpath("//*[@id='block-teamleaders-main-menu']/div/aside/ul/li[6]/a");
    private final By resourcesSearchField = By.xpath("//*[@id='edit-search-api-fulltext']");
    private final By loadingIndicator = By.cssSelector(".ajax-progress, .throbber");

    public ProjectResourcePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickResourcesButton() {
        WebElement resourcesLink = wait.until(ExpectedConditions.presenceOfElementLocated(resourcesButtonLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", resourcesLink);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(resourcesButtonLink)).click();
        } catch (Exception clickError) {
            // The SVG area can intercept normal clicks; JS click keeps this step stable.
            WebElement svg = wait.until(ExpectedConditions.presenceOfElementLocated(resourcesButtonSvg));
            ((JavascriptExecutor) driver).executeScript("arguments[0].closest('a').click();", svg);
        }
        System.out.println("[ProjectResourcePage] Clicked Resources button.");
    }

    public void searchResources(String query) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(resourcesSearchField));
        field.clear();
        field.sendKeys(query);
        field.sendKeys(Keys.ENTER);
        waitForAjaxToSettle();
        System.out.println("[ProjectResourcePage] Searched resources for: " + query);
    }

    public boolean selectResultContaining(String title) {
        By resultLinks = By.xpath(
            "//*[@id='tlMainContent']//a[contains(normalize-space(.),\"" + title + "\")]"
                + " | //a[contains(normalize-space(.),\"" + title + "\")]"
        );

        try {
            waitForAjaxToSettle();
            List<WebElement> links = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(resultLinks));
            if (links.isEmpty()) {
                return false;
            }

            WebElement firstMatch = wait.until(ExpectedConditions.elementToBeClickable(links.get(0)));
            firstMatch.click();
            System.out.println("[ProjectResourcePage] Selected resource result: " + title);
            return true;
        } catch (Exception e) {
            System.out.println("[ProjectResourcePage] No selectable result found for: " + title + " - " + e.getMessage());
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private void waitForAjaxToSettle() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));
        } catch (TimeoutException ignored) {
            // Continue even if no spinner is found.
        }
    }
}
