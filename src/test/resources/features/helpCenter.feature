Feature: Help Center from Youth Friendly

  As a logged-in user
  I want to open the Help Center from the Youth Friendly flow
  So that I can verify the Help Center page content

  @Helpcenter
  Scenario: Verify Help Center content from Youth Friendly
    Given I log in for youth friendly flow with username "abel.wondwosen+test@newyorkcares.org" and password "Habesha#12"
    When I click the Youth Friendly button
    And I click the Help Center button
    Then I should see the help center heading "Questions? We've got answers"

