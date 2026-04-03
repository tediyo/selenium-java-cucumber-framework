package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for New York Cares Login Page.
 * Encapsulates all interactions with the login form.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators (XPath as provided)
    private final By usernameField = By.xpath("//*[@id='edit-name']");
    private final By passwordField = By.xpath("//*[@id='edit-pass']");
    private final By loginButton   = By.xpath("//*[@id='edit-submit']");

    private static final String LOGIN_URL     = "https://nycares:Volunteer87@test-sfup.newyorkcares.org/user/login";
    private static final String DASHBOARD_URL = "https://www.newyorkcares.org/dashboard";

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Navigate to the New York Cares login page.
     */
    public void open() {
        driver.get(LOGIN_URL);
        System.out.println("[LoginPage] Navigated to: " + LOGIN_URL);
    }

    /**
     * Enter the username or email into the username field.
     */
    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(usernameField));
        field.clear();
        field.sendKeys(username);
        System.out.println("[LoginPage] Entered username: " + username);
    }

    /**
     * Enter the password into the password field.
     */
    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        field.clear();
        field.sendKeys(password);
        System.out.println("[LoginPage] Entered password: [PROTECTED]");
    }

    /**
     * Click the login/submit button.
     */
    public void clickLoginButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        button.click();
        System.out.println("[LoginPage] Clicked the Login button.");
    }

    /**
     * Check whether the browser has been redirected to the dashboard URL.
     * Waits up to 30 seconds for the URL to contain the dashboard path.
     */
    public boolean isOnDashboard() {
        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            String currentUrl = driver.getCurrentUrl();
            System.out.println("[LoginPage] Current URL after login: " + currentUrl);
            return currentUrl.contains("/dashboard");
        } catch (Exception e) {
            System.out.println("[LoginPage] Did NOT reach dashboard. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    /**
     * Get the current URL for assertion purposes.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Wait after landing on the dashboard (successful login).
     */
    public void waitAfterSuccessfulLogin() {
        pauseSeconds(1);
    }

    private static void pauseSeconds(long seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
