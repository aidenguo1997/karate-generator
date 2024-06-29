package pers.jie.karate.generator;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;
import java.util.Map;

import pers.jie.karate.core.OperationHandler;

public class KarateElementGenerator {
    private final OperationHandler operationHandler;

    public KarateElementGenerator(OperationHandler operationHandler) {
        this.operationHandler = operationHandler;
    }

    public void backgroundKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, List<String> errors, StringBuilder karateScript) {
        operationHandler.handleOperation(openAPI, gherkinContent, requestDataList, true, karateScript, errors);
    }

    public void scenarioKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, List<String> errors, StringBuilder karateScript) {
        operationHandler.handleOperation(openAPI, gherkinContent, requestDataList, false, karateScript, errors);
    }
}
