package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for New York Cares home → Search Projects → fulltext search.
 */
public class SearchProjectsPage {

    private static final String HOME_URL = "https://www.newyorkcares.org/";
    // private static final String HOME_URL = "https://nycares:Volunteer87@test-sfup.newyorkcares.org";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchProjectsNav =
        By.xpath("/html/body/div[1]/div/header/div[1]/div[2]/div[2]/ul[1]/li[5]/a");
    private final By fulltextField = By.id("fulltext");

    public SearchProjectsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void openHome() {
        try {
            driver.get(HOME_URL);
        } catch (TimeoutException e) {
            // Full load can hang on third-party assets; stop so the DOM stays usable for steps.
            ((JavascriptExecutor) driver).executeScript("window.stop();");
            System.out.println("[SearchProjectsPage] Page load timed out; stopped and continuing.");
        }
        try {
            System.out.println("[SearchProjectsPage] Opened home: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("[SearchProjectsPage] Opened home (could not read URL): " + e.getMessage());
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    public void clickSearchProjects() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(searchProjectsNav));
        link.click();
        System.out.println("[SearchProjectsPage] Clicked Search Projects.");
    }

    /**
     * Types the query into #fulltext and submits (Enter).
     */
    public void searchFulltext(String query) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(fulltextField));
        field.clear();
        field.sendKeys(query);
        field.sendKeys(Keys.ENTER);
        System.out.println("[SearchProjectsPage] Submitted fulltext search: " + query);
    }

    /**
     * Clicks a result link whose visible text contains the project title.
     *
     * @return true if an element was clicked
     */
    public boolean clickProjectResultContaining(String projectTitle) {
        String xpath = "//a[contains(normalize-space(.), '" + projectTitle + "')]";
        By linkBy = By.xpath(xpath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait resultWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        final int maxAttempts = 8;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // Re-query every attempt — search results re-render via AJAX (long overlay waits caused stale refs).
                WebElement link = resultWait.until(ExpectedConditions.elementToBeClickable(linkBy));
                js.executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                    link
                );
                link = resultWait.until(ExpectedConditions.elementToBeClickable(linkBy));
                js.executeScript("arguments[0].click();", link);

                pauseSeconds(12);

                System.out.println("[SearchProjectsPage] Clicked result for: " + projectTitle);
                return true;
            } catch (StaleElementReferenceException e) {
                System.out.println(
                    "[SearchProjectsPage] Stale result link after DOM refresh; retry "
                        + attempt + "/" + maxAttempts
                );
            } catch (Exception e) {
                System.out.println(
                    "[SearchProjectsPage] Could not click result for: " + projectTitle + " — " + e.getMessage()
                );
                return false;
            }
        }
        System.out.println("[SearchProjectsPage] Gave up after " + maxAttempts + " attempts: " + projectTitle);
        return false;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private static void pauseSeconds(long seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
