package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Google Search Page.
 * Encapsulates all interactions with google.com
 */
public class GoogleSearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By searchBox      = By.name("q");
    private final By searchResults  = By.id("search");
    private final By firstResult    = By.cssSelector("h3");

    public GoogleSearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Navigate to Google homepage
     */
    public void open() {
        driver.get("https://www.google.com");
        System.out.println("[GoogleSearchPage] Opened: " + driver.getCurrentUrl());
    }

    /**
     * Type a search term and submit
     */
    public void searchFor(String term) {
        WebElement box = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        box.clear();
        box.sendKeys(term);
        box.sendKeys(Keys.ENTER);
        System.out.println("[GoogleSearchPage] Searched for: " + term);
    }

    /**
     * Wait for results page to appear and return page title
     */
    public String getPageTitle() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchResults));
        return driver.getTitle();
    }

    /**
     * Check whether the results container is displayed
     */
    public boolean areResultsDisplayed() {
        try {
            WebElement results = wait.until(
                ExpectedConditions.visibilityOfElementLocated(searchResults)
            );
            return results.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the text of the first search result heading
     */
    public String getFirstResultTitle() {
        WebElement first = wait.until(
            ExpectedConditions.visibilityOfElementLocated(firstResult)
        );
        return first.getText();
    }

    /**
     * Get the current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
