Feature: Youth Friendly Family Roster

  As a logged-in user
  I want to open the Youth Friendly family roster flow
  So that I can verify the family member account page

  @youthfriendly
  Scenario: Verify Create a family member account page from Youth Friendly
    Given I log in for youth friendly flow with username "abel.wondwosen+test@newyorkcares.org" and password "Habesha#12"
    When I click the Youth Friendly button
    And I click the Family Roaster button
    Then I should see the youth friendly heading "Create a family member account"
