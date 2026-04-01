package com.testfm.hooks;

import com.testfm.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Cucumber Hooks — runs Before and After each scenario.
 * Handles WebDriver initialization and teardown.
 */
public class Hooks {

    /**
     * Runs BEFORE every scenario.
     * Ensures a fresh WebDriver is ready.
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        System.out.println("\n========================================");
        System.out.println("[Hook] Starting Scenario: " + scenario.getName());
        System.out.println("[Hook] Tags: " + scenario.getSourceTagNames());
        System.out.println("========================================");
        // Initialize driver (lazy init inside DriverManager)
        DriverManager.getDriver();
    }

    /**
     * Runs AFTER every scenario.
     * Captures status and quits the driver.
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        System.out.println("========================================");
        System.out.println("[Hook] Scenario: " + scenario.getName());
        System.out.println("[Hook] Status:   " + scenario.getStatus());
        System.out.println("========================================\n");
        DriverManager.quitDriver();
    }
}
