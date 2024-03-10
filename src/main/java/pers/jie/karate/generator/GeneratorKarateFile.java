package pers.jie.karate.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratorKarateFile {
    public static void main(String[] args) {
        convertGherkinToKarate("src/test/resources/Features/2.feature", "src/main/resources/Swaggers/2.json", "src/main/resources/Data/2.json");
    }

    public static void convertGherkinToKarate(String gherkinPath, String swaggerPath, String jsonFilePath) {
        try {
            // Parse Gherkin content
            String gherkinContent = new String(Files.readAllBytes(Path.of(gherkinPath)));
            // Parse Swagger JSON using io.swagger.v3
            SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(swaggerPath, null, null);
            OpenAPI openAPI = parseResult.getOpenAPI();
            // Read JSON file to get request data
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> requestDataList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, String>>>() {
            });
            // Generate Karate feature file content
            String karateScript = generateKarateScript(openAPI, gherkinContent, requestDataList);
            writeKarateScriptToFile(karateScript);
            System.out.println("Generated Karate Script:\n" + karateScript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeKarateScriptToFile(String karateScript) {
        try {
            Path path = Path.of("src/test/java/pers/jie/karate/api/karate-script.feature");
            Files.write(path, karateScript.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Karate script written to: " + path.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        List<String> errors = new ArrayList<>();
        // Get title from Swagger info
        String swaggerTitle = openAPI.getInfo().getTitle();
        // Feature declaration outside the loop
        karateScript.append(String.format("Feature: %s\n", swaggerTitle));
        // Background declaration
        karateScript.append("  Background:\n");
        karateScript.append(String.format("    * url '%s'\n", openAPI.getServers().get(0).getUrl())); // Adding the base URL
        // Loop through paths
        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            // Loop through operations for each path
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                if (operation.getSummary() == null) {
                    errors.add("Operation summary is null for path: " + path + ", HTTP method: " + httpMethod);
                    return; // Skip this operation if summary is null
                }
                List<String> gherkinTags = getGherkinTags(gherkinContent, operation.getSummary());
                for (String gherkinTag : gherkinTags) {
                    if (!operation.getSummary().equals(gherkinTag)) {
                        errors.add(String.format("Mismatch between Gherkin tag '%s' and Swagger operation summary '%s'", gherkinTag, operation.getSummary()));
                        System.out.println(gherkinTag + " " + operation.getSummary());
                        continue;
                    }
                    handleOperation(karateScript, path, operation, gherkinTag, requestDataList, httpMethod.name());
                }
            });
        }

        if (!errors.isEmpty()) {
            System.out.println("Errors encountered:");
            errors.forEach(System.out::println);
        }

        return karateScript.toString();
    }

    private static void handleOperation(StringBuilder karateScript, String path, Operation operation, String gherkinTag, List<Map<String, String>> requestDataList, String httpMethod) {
        if (gherkinTag.equals("#login")) {
            karateScript.append(String.format("    #根據Karate規範，如後續Scenario需登入，則Scenario: %s將移入Background\n",  operation.getDescription()));
            karateScript.append(String.format("    Given path '%s'\n", path));
        } else {
            karateScript.append(String.format("\n  Scenario: %s\n", operation.getDescription()));
            karateScript.append(String.format("    Given path '%s'\n", path));
        }

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

    private static List<String> getGherkinTags(String gherkinContent, String summary) {
        String[] lines = gherkinContent.split("\n");
        List<String> gherkinTags = new ArrayList<>();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("#") && summary != null && trimmedLine.contains(summary)) {
                gherkinTags.add(trimmedLine.trim());
            }
        }

        if (gherkinTags.isEmpty()) {
            // If no matching tags found, add a default tag
            gherkinTags.add("summary");
        }

        return gherkinTags;
    }
}
