package com.taskmanager.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskSteps {

    @Autowired
    private TestContext context;

    @Given("I am logged in as user {string} with password {string}")
    public void iAmLoggedInAsUserWithPassword(String username, String password) {
        String uniqueUsername = username + "_" + System.currentTimeMillis();
        context.lastToken = registerAndLogin(uniqueUsername, password);
        context.lastUsername = uniqueUsername;
    }

    @Given("user {string} has created a task titled {string}")
    public void userHasCreatedATaskTitled(String username, String title) {
        String token = registerAndLogin(username, "secret123");
        createTask(token, title, "TODO", "MEDIUM");
    }

    @Given("I have created a task titled {string} with status {string} and priority {string}")
    public void iHaveCreatedATaskTitledWithStatusAndPriority(String title, String status, String priority) {
        Response created = createTask(context.lastToken, title, status, priority);
        context.lastCreatedTaskId = created.jsonPath().getLong("id");
    }

    @When("I create a task titled {string} with status {string} and priority {string}")
    public void iCreateATaskTitledWithStatusAndPriority(String title, String status, String priority) {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("status", status);
        body.put("priority", priority);

        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/tasks");

        Long id = context.lastResponse.jsonPath().getLong("id");
        if (id != null) {
            context.lastCreatedTaskId = id;
        }
    }

    @When("I update that task's status to {string}")
    public void iUpdateThatTaskSStatusTo(String status) {
        Map<String, String> body = new HashMap<>();
        body.put("title", "updated via test"); // title is required by validation
        body.put("status", status);

        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .contentType("application/json")
                .body(body)
                .when()
                .put("/api/tasks/" + context.lastCreatedTaskId);
    }

    @When("I delete that task")
    public void iDeleteThatTask() {
        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .when()
                .delete("/api/tasks/" + context.lastCreatedTaskId);
    }

    @When("I request my task list")
    public void iRequestMyTaskList() {
        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .when()
                .get("/api/tasks");
    }

    @When("I request my tasks filtered by status {string}")
    public void iRequestMyTasksFilteredByStatus(String status) {
        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .queryParam("status", status)
                .when()
                .get("/api/tasks");
    }

    @When("I request my tasks filtered by priority {string}")
    public void iRequestMyTasksFilteredByPriority(String priority) {
        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .queryParam("priority", priority)
                .when()
                .get("/api/tasks");
    }

    @When("I request my tasks filtered by status {string} and priority {string}")
    public void iRequestMyTasksFilteredByStatusAndPriority(String status, String priority) {
        context.lastResponse = given()
                .header("Authorization", "Bearer " + context.lastToken)
                .queryParam("status", status)
                .queryParam("priority", priority)
                .when()
                .get("/api/tasks");
    }

    @Then("the task should be created successfully")
    public void theTaskShouldBeCreatedSuccessfully() {
        context.lastResponse.then().statusCode(201);
    }

    @Then("the created task should have status {string} and priority {string}")
    public void theCreatedTaskShouldHaveStatusAndPriority(String status, String priority) {
        assertThat(context.lastResponse.jsonPath().getString("status")).isEqualTo(status);
        assertThat(context.lastResponse.jsonPath().getString("priority")).isEqualTo(priority);
    }

    @Then("the task's status should be {string}")
    public void theTaskSStatusShouldBe(String status) {
        assertThat(context.lastResponse.jsonPath().getString("status")).isEqualTo(status);
    }

    @Then("the task should no longer exist")
    public void theTaskShouldNoLongerExist() {
        context.lastResponse.then().statusCode(204);

        given()
                .header("Authorization", "Bearer " + context.lastToken)
                .when()
                .get("/api/tasks/" + context.lastCreatedTaskId)
                .then()
                .statusCode(404);
    }

    @Then("I should not see a task titled {string}")
    public void iShouldNotSeeATaskTitled(String title) {
        java.util.List<String> titles = context.lastResponse.jsonPath().getList("title", String.class);
        assertThat(titles).doesNotContain(title);
    }

    @Then("I should receive {int} task\\(s\\)")
    public void iShouldReceiveTasks(int expectedCount) {
        java.util.List<?> tasks = context.lastResponse.jsonPath().getList("$");
        assertThat(tasks.size()).isEqualTo(expectedCount);
    }



    private String registerAndLogin(String username, String password) {
        Map<String, String> registerBody = Map.of(
                "username", username,
                "email", username + "@example.com",
                "password", password
        );

        Response registerResponse = given()
                .contentType("application/json")
                .body(registerBody)
                .when()
                .post("/api/auth/register");


        if (registerResponse.statusCode() == 201) {
            return registerResponse.jsonPath().getString("token");
        }


        Map<String, String> loginBody = Map.of("username", username, "password", password);

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        return loginResponse.jsonPath().getString("token");
    }

    private Response createTask(String token, String title, String status, String priority) {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("status", status);
        body.put("priority", priority);

        return given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/tasks");
    }
}