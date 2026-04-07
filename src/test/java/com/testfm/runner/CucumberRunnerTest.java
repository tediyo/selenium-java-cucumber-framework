package com.testfm.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber Test Runner using JUnit 5 Platform Suite.
 *
 * Run all tests :  mvn test
 * Run by tag    :  mvn test -Dcucumber.filter.tags="@smoke"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/report.html, json:target/cucumber-reports/report.json, com.testfm.reporter.CustomHtmlReporter:target/custom-reports")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.testfm.steps, com.testfm.hooks")
@ConfigurationParameter(key = EXECUTION_DRY_RUN_PROPERTY_NAME,
        value = "false")
public class CucumberRunnerTest {
    // This class intentionally left empty.
    // JUnit Platform Suite picks it up via annotations above.
}
