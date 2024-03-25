package pers.jie.karate.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratorScenarioScript {
    public static StringBuilder generateScenarioKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        List<String> errors = new ArrayList<>();
        // Loop through paths
        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            // Loop through operations for each path
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                String gherkinTag = GherkinTag.getGherkinTag(gherkinContent, httpMethod, operation, errors, path);
                if (gherkinTag == null) return;
                handleOperation(karateScript, path, operation, gherkinTag, requestDataList, httpMethod.name());
            });
        }

        if (!errors.isEmpty()) {
            System.out.println("Errors encountered:");
            errors.forEach(System.out::println);
        }
        return karateScript;
    }

    private static void handleOperation(StringBuilder karateScript, String path, Operation operation, String gherkinTag, List<Map<String, String>> requestDataList, String httpMethod) {
        if (!gherkinTag.equals("#session")) {
            karateScript.append(String.format("\n  Scenario: %s\n", operation.getDescription()));
            karateScript.append(String.format("    Given path '%s'\n", path));
            // Handle request body if present
            if (operation.getParameters() != null) {
                for (Map<String, String> requestData : requestDataList) {
                    String requestDataPath = requestData.get("path");
                    String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
                    String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
                    if (normalizedRequestPath.equals(normalizedPath) && requestData.get("method").equals(httpMethod)) {
                        String requestText = requestData.get("request_text");
                        // 检查参数的 'in' 属性是否为 'query'，如果是，则解析成 'And param'，否则解析成 'And request'
                        if ("query".equals(operation.getParameters().get(0).getIn())) {
                            String[] keyValuePairs = requestText.split("&");
                            for (String pair : keyValuePairs) {
                                String[] parts = pair.split("=");
                                if (parts.length == 2) {
                                    karateScript.append(String.format("    And param %s = '%s'\n", parts[0], parts[1]));
                                }
                            }
                        } else {
                            ObjectNode jsonRequest = new ObjectMapper().createObjectNode();
                            String[] keyValuePairs = requestText.split("&");
                            for (String pair : keyValuePairs) {
                                String[] parts = pair.split("=");
                                if (parts.length == 2) {
                                    jsonRequest.put(parts[0], parts[1]);
                                }
                            }
                            karateScript.append(String.format("    And request %s\n", jsonRequest.toString()));
                        }
                        break;
                    }
                }
            }

            // Handle responses
            operation.getResponses().forEach((statusCode, response) -> {
                karateScript.append(String.format("    When method %s\n", httpMethod.toLowerCase()));
                karateScript.append(String.format("    Then status %s\n", statusCode));
            });
        }
    }
}
