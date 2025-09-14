package it.dtd.note.bdd.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ScenarioScope
public class TestContext {
    private String jwt;
    private Map<String, Object> vars = new HashMap<>();
    private Response lastResponse;
}
