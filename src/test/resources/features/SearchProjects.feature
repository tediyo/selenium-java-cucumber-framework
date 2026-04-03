Feature: Search volunteer projects on New York Cares

  As a visitor
  I want to search for a volunteer project by name
  So that I can open it from the results

  @searchProjects
  Scenario: Search fulltext and open Prepare Food project
    Given I navigate to the New York Cares home page
    When I click the Search Projects button
    And I search for "Prepare Food Indoors for Distribution" in the fulltext field
    Then I select the searched project "Prepare Food Indoors for Distribution" if it appears in the results
