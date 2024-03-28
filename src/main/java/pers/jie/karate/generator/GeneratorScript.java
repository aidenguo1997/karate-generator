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
    public StringBuilder backgroundKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        handleOperation.handleOperation(openAPI, gherkinContent, requestDataList, true, karateScript);
        return karateScript;
    }

    public StringBuilder scenarioKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        handleOperation.handleOperation(openAPI, gherkinContent, requestDataList, false, karateScript);
        return karateScript;
    }
}
