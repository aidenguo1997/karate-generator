package pers.jie.karate.generator;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;
import java.util.Map;

import pers.jie.karate.core.HandleOperation;

public class GeneratorScript {
    private final HandleOperation handleOperation;

    public GeneratorScript(HandleOperation handleOperation) {
        this.handleOperation = handleOperation;
    }

    public void backgroundKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, List<String> errors, StringBuilder karateScript) {
        handleOperation.handleOperation(openAPI, gherkinContent, requestDataList, true, karateScript, errors);
    }

    public void scenarioKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, List<String> errors, StringBuilder karateScript) {
        handleOperation.handleOperation(openAPI, gherkinContent, requestDataList, false, karateScript, errors);
    }
}
