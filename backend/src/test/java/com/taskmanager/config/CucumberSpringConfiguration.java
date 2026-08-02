package com.taskmanager.config;

import com.taskmanager.TaskManagerApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.PostConstruct;

@CucumberContextConfiguration
@SpringBootTest(
        classes = TaskManagerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @LocalServerPort
    private int port;


    @PostConstruct
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
}