package com.taskmanager.steps;

import com.taskmanager.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestContext context;

    @Given("no user is registered with username {string}")
    public void noUserIsRegisteredWithUsername(String username) {
        assertThat(userRepository.existsByUsername(username)).isFalse();
    }

    @Given("a user is already registered with username {string}")
    public void aUserIsAlreadyRegisteredWithUsername(String username) {
        registerUser(username, username + "@example.com", "secret123");
    }

    @Given("a user is already registered with username {string} and password {string}")
    public void aUserIsAlreadyRegisteredWithUsernameAndPassword(String username, String password) {
        registerUser(username, username + "@example.com", password);
    }

    @When("I register with username {string}, email {string} and password {string}")
    public void iRegisterWithUsernameEmailAndPassword(String username, String email, String password) {
        Map<String, String> body = Map.of("username", username, "email", email, "password", password);

        context.lastResponse = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @When("I log in with username {string} and password {string}")
    public void iLogInWithUsernameAndPassword(String username, String password) {
        Map<String, String> body = Map.of("username", username, "password", password);

        context.lastResponse = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/auth/login");
    }

    @Then("the registration should succeed")
    public void theRegistrationShouldSucceed() {
        context.lastResponse.then().statusCode(201);
    }

    @Then("the registration should fail with status {int}")
    public void theRegistrationShouldFailWithStatus(int expectedStatus) {
        context.lastResponse.then().statusCode(expectedStatus);
    }

    @Then("the login should succees")
    public void theLoginShouldSucceed() {
        context.lastResponse.then().statusCode(200);
    }

    @Then("the login should fail with status {int}")
    public void theLoginShouldFailWithStatus(int expectedStatus) {
        context.lastResponse.then().statusCode(expectedStatus);
    }

    @Then("I should receive a valid JWT token")
    public void iShouldReceiveAValidJwtToken() {
        String token = context.lastResponse.jsonPath().getString("token");
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    private void registerUser(String username, String email, String password) {
        Map<String, String> body = Map.of("username", username, "email", email, "password", password);
        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/auth/register");
    }
}