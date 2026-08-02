package com.taskmanager.steps;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class TestContext {

    public Response lastResponse;
    public String lastToken;
    public String lastUsername;
    public Long lastCreatedTaskId;
}