Feature: User authentication
  As a new user
  I want to register and log in
  So that I can access my personal task list

  Scenario: Successful registration
    Given no user is registered with username "khawaga"
    When I register with username "khawaga", email "khawaga@example.com" and password "khawaga123"
    Then the registration should succeed
    And I should receive a valid JWT token

  Scenario: Registration fails with a duplicate username
    Given a user is already registered with username "elkhawaga"
    When I register with username "elkhawaga", email "elkhawaga2@example.com" and password "elkhawaga123"
    Then the registration should fail with status 409

  Scenario: Successful login
    Given a user is already registered with username "batman" and password "batman123"
    When I log in with username "batman" and password "batman123"
    Then the login should succees
    And I should receive a valid JWT token

  Scenario: Login fails with wrong password
    Given a user is already registered with username "dave" and password "secret123"
    When I log in with username "dave" and password "wrong-password"
    Then the login should fail with status 401
