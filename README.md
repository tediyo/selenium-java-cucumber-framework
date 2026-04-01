# ⬡ TestFM — Selenium Cucumber BDD Framework

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.27-43B02A?style=for-the-badge&logo=selenium&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber-7.20-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit-5.10-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Edge](https://img.shields.io/badge/Edge-Browser-0078D7?style=for-the-badge&logo=microsoftedge&logoColor=white)

**A production-grade BDD test automation framework** built with **Selenium WebDriver**, **Cucumber Gherkin**, and **JUnit 5**  
Featuring a stunning custom HTML report with light/dark mode 🌙 ☀️

</div>

---

## 📌 Overview

TestFM is a clean, maintainable test automation framework following the **Behavior-Driven Development (BDD)** approach. It uses **Gherkin** feature files to define test scenarios in plain English, making them accessible to non-technical stakeholders while keeping the implementation robust for engineers.

### ✨ Key Features

- 🧪 **BDD with Cucumber** — Write tests in plain English using Gherkin syntax
- 🌐 **Selenium WebDriver** — Browser automation with Microsoft Edge
- 📄 **Page Object Model (POM)** — Clean separation of page interactions and test logic
- 📊 **Custom HTML Report** — Interactive, self-contained report with donut charts, search, filters
- 🌙 **Light / Dark Mode** — Toggle between themes with SVG icons and `localStorage` persistence
- 🏷️ **Tag-based Filtering** — Run specific test subsets using Cucumber tags (`@smoke`, `@search`)
- ⚡ **Step Performance Bars** — Visual indicators showing step execution speed (fast/medium/slow)
- 🔍 **Scenario Search** — Real-time search through scenarios in the HTML report

---

## 🏗️ Project Structure

```
TestFM/
├── drivers/
│   └── msedgedriver.exe              # Local Edge WebDriver binary
├── src/
│   ├── main/java/com/testfm/
│   │   ├── driver/
│   │   │   └── DriverManager.java    # WebDriver lifecycle management
│   │   └── pages/
│   │       └── GoogleSearchPage.java # Page Object for Google Search
│   └── test/
│       ├── java/com/testfm/
│       │   ├── hooks/
│       │   │   └── Hooks.java        # @Before / @After scenario hooks
│       │   ├── reporter/
│       │   │   └── CustomHtmlReporter.java  # Custom Cucumber HTML reporter
│       │   ├── runner/
│       │   │   └── CucumberRunnerTest.java  # JUnit 5 Suite runner
│       │   └── steps/
│       │       └── GoogleSearchSteps.java   # Step definitions
│       └── resources/
│           ├── features/
│           │   └── GoogleSearch.feature     # Gherkin feature file
│           └── junit-platform.properties
├── target/
│   ├── custom-reports/index.html     # ✨ Custom HTML report output
│   ├── cucumber-reports/             # JSON + default HTML reports
│   └── cucumber-html-reports/        # Masterthought report
└── pom.xml
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|:------|:-----------|:--------|
| **Language** | Java (OpenJDK) | 21 |
| **Build Tool** | Apache Maven | 3.x |
| **Browser Automation** | Selenium WebDriver | 4.27.0 |
| **BDD Framework** | Cucumber Java | 7.20.1 |
| **Test Runner** | JUnit 5 Platform Suite | 5.10.1 |
| **Browser** | Microsoft Edge | Latest |
| **Reporting** | Custom HTML + Masterthought | 5.8.1 |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21+** installed and `JAVA_HOME` set
- **Maven 3.x+** installed
- **Microsoft Edge** browser installed
- **Edge WebDriver** (`msedgedriver.exe`) in the `drivers/` folder  
  → Download from [Microsoft Edge WebDriver](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)  
  → Match the version to your Edge browser version

### Installation

```bash
# Clone the repository
git clone https://github.com/tediyo/selenium-java-cucumber-framework.git
cd selenium-java-cucumber-framework

# Install dependencies
mvn clean install -DskipTests
```

---

## ▶️ Running Tests

### Run all tests
```bash
mvn clean verify
```

### Run tests by tag
```bash
# Run only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run only search tests
mvn test -Dcucumber.filter.tags="@search"

# Run multiple tags
mvn test -Dcucumber.filter.tags="@smoke and @search"
```

### Run in headless mode
Uncomment the headless line in `DriverManager.java`:
```java
options.addArguments("--headless");
```

---

## 📊 Reports

After running tests, **three types of reports** are generated:

| Report | Location | Description |
|:-------|:---------|:------------|
| **Custom HTML** | `target/custom-reports/index.html` | ⭐ Interactive report with charts, search, light/dark mode |
| **Cucumber HTML** | `target/cucumber-reports/report.html` | Default Cucumber report |
| **Masterthought** | `target/cucumber-html-reports/` | Detailed Masterthought report |

### Custom HTML Report Features

- 📈 **Donut chart** showing pass/fail/skip ratio
- 🔍 **Live search** across all scenarios
- 🏷️ **Tag-based filters** (e.g., `@smoke`, `@search`)
- 🌙☀️ **Light / Dark mode toggle** with localStorage persistence
- ⏱️ **Performance bars** per step (green = fast, amber = medium, red = slow)
- 📋 **Expandable features & scenarios** with detailed step-by-step results
- 🐛 **Error messages** displayed inline for failed steps

---

## 📝 Writing New Tests

### 1. Create a Feature File

```gherkin
# src/test/resources/features/MyFeature.feature
Feature: My Feature

  @smoke
  Scenario: My test scenario
    Given I do something
    When something happens
    Then I should see the result
```

### 2. Create a Page Object

```java
// src/main/java/com/testfm/pages/MyPage.java
public class MyPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public MyPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Add your page interactions here
}
```

### 3. Create Step Definitions

```java
// src/test/java/com/testfm/steps/MySteps.java
public class MySteps {
    private final MyPage myPage;

    public MySteps() {
        this.myPage = new MyPage(DriverManager.getDriver());
    }

    @Given("I do something")
    public void iDoSomething() {
        // Implementation
    }
}
```

---

## 🏛️ Architecture

```
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

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<div align="center">

Built with ☕ and 💜 by **TestFM**

</div>
