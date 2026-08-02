Feature: Filtering tasks
  As a logged in user
  I want to filter my tasks by status and priority
  So that I can focus on what matters right now

  Background:
    Given I am logged in as user "sarah" with password "sarah123"
    And I have created a task titled "Task A" with status "TODO" and priority "HIGH"
    And I have created a task titled "Task B" with status "DONE" and priority "LOW"
    And I have created a task titled "Task C" with status "TODO" and priority "LOW"

  Scenario: Filter tasks by status
    When I request my tasks filtered by status "TODO"
    Then I should receive 2 task(s)

  Scenario: Filter tasks by priority
    When I request my tasks filtered by priority "LOW"
    Then I should receive 2 task(s)

  Scenario: Filter tasks by status and priority together
    When I request my tasks filtered by status "TODO" and priority "LOW"
    Then I should receive 1 task(s)
