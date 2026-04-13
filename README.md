# TestFM - Selenium Cucumber BDD Framework

TestFM is a Java 21 test automation framework using Selenium WebDriver, Cucumber, and JUnit 5.
It currently automates user journeys for Google Search and New York Cares flows (Login, Search Projects, Update Account) with custom HTML reporting.

## Overview

- BDD style with `.feature` files under `src/test/resources/features`
- Page Object Model under `src/main/java/com/testfm/pages`
- JUnit Platform Cucumber suite runner in `src/test/java/com/testfm/runner/CucumberRunnerTest.java`
- Hooks for driver lifecycle and scenario-level behavior in `src/test/java/com/testfm/hooks/Hooks.java`
- Three report outputs: Cucumber HTML, Cucumber JSON, and a custom interactive report

## Tech Stack

- Java 21
- Maven 3.x
- Selenium 4.27.0
- Cucumber 7.20.1
- JUnit Platform Suite 1.10.1 / JUnit 5.10.1
- Microsoft Edge + local `msedgedriver.exe`

## Project Structure

```text
TestFM/
├── drivers/
│   └── msedgedriver.exe
├── src/
│   ├── main/java/com/testfm/
│   │   ├── driver/
│   │   │   └── DriverManager.java
│   │   └── pages/
│   │       ├── GoogleSearchPage.java
│   │       ├── LoginPage.java
│   │       ├── SearchProjectsPage.java
│   │       └── UpdateAccountPage.java
│   └── test/
│       ├── java/com/testfm/
│       │   ├── hooks/Hooks.java
│       │   ├── reporter/CustomHtmlReporter.java
│       │   ├── runner/CucumberRunnerTest.java
│       │   └── steps/
│       │       ├── GoogleSearchSteps.java
│       │       ├── LoginSteps.java
│       │       ├── SearchGoogleSteps.java
│       │       └── UpdateAccountSteps.java
│       └── resources/
│           ├── features/
│           │   ├── GoogleSearch.feature
│           │   ├── Login.feature
│           │   ├── SearchProjects.feature
│           │   └── updateAccount.feature
│           └── junit-platform.properties
├── target/
└── pom.xml
```

## Prerequisites

- Java 21+ installed (`JAVA_HOME` configured)
- Maven 3.x installed
- Microsoft Edge installed
- Edge WebDriver binary at `drivers/msedgedriver.exe` that matches your Edge version

WebDriver download:
[Microsoft Edge WebDriver](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)

## Install

```bash
mvn clean install -DskipTests
```

## Run Tests

Run full suite:

```bash
mvn clean verify
```

Run specific tag:

```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@searchProjects"
mvn test -Dcucumber.filter.tags="@Login"
mvn test -Dcucumber.filter.tags="@updateAccount"
```

Run with tag expression:

```bash
mvn test -Dcucumber.filter.tags="@smoke and not @updateAccount"
```

### Headless Mode

In `src/main/java/com/testfm/driver/DriverManager.java`, uncomment:

```java
options.addArguments("--headless");
```

## Current Features and Tags

- `GoogleSearch.feature`: `@smoke @search`
- `Login.feature`: `@Login`
- `SearchProjects.feature`: `@searchProjects`
- `updateAccount.feature`: `@updateAccount`

## Reports

After a run, reports are generated at:

- `target/cucumber-reports/report.html` (Cucumber HTML)
- `target/cucumber-reports/report.json` (Cucumber JSON)
- `target/cucumber-html-reports/` (Masterthought report)
- `target/custom-reports/*_Report.html` (Custom interactive report, per feature)

Custom report includes:

- pass/fail/skipped summary cards
- donut chart pass rate
- searchable scenarios
- tag filters
- step duration performance bars
- inline error details
- light/dark theme toggle

## Notes

- The driver path is set explicitly in `DriverManager`:
  `System.getProperty("user.dir") + "\\drivers\\msedgedriver.exe"`.
- `Hooks` initializes and quits the browser for every scenario.
- `@updateAccount` scenarios have an additional `@AfterStep` delay to improve stability.

## Security Reminder

Some feature files currently include real-looking test credentials and URLs.
For shared/public repositories, move credentials to environment variables or a secure secret manager before publishing.

---

## Architecture

```text
┌──────────────────────────────────────────────────────────┐
│                    Feature Files (.feature)               │
│              Written in Gherkin (plain English)           │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│                  Step Definitions (Java)                   │
│           Maps Gherkin steps → Java methods                │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│                  Page Objects (POM)                        │
│        Encapsulates UI element locators & actions          │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│                  Driver Manager                           │
│            WebDriver lifecycle & configuration             │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│                  Microsoft Edge Browser                    │
│              Automated via Selenium WebDriver              │
└──────────────────────────────────────────────────────────┘
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).

---

<div align="center">
Built with ☕ and 💜 by **TestFM**
</div>
