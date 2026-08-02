Feature: Task management
  As a logged in user
  I want to create, view, update and delete my tasks
  So that I can keep track of my work

  Background:
    Given I am logged in as user "ziad" with password "password1233"

  Scenario: Create a new task
    When I create a task titled "Write report" with status "TODO" and priority "HIGH"
    Then the task should be created successfully
    And the created task should have status "TODO" and priority "HIGH"

  Scenario: Update an existing task's status
    Given I have created a task titled "Fix bug" with status "TODO" and priority "MEDIUM"
    When I update that task's status to "IN_PROGRESS"
    Then the task's status should be "IN_PROGRESS"

  Scenario: Delete a task
    Given I have created a task titled "Old task" with status "TODO" and priority "LOW"
    When I delete that task
    Then the task should no longer exist

  Scenario: A user cannot see another user's tasks
    Given user "mshziad" has created a task titled "mshziad's secret task"
    When I request my task list
    Then I should not see a task titled "mshziad's secret task"
