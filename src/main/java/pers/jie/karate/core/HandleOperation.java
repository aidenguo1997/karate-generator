package pers.jie.karate.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import pers.jie.karate.tag.GherkinTag;
import pers.jie.karate.param.KarateSyntaxParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandleOperation {

    public void handleOperation(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, boolean isBackground, StringBuilder karateScript) {
        // 参数验证
        if (openAPI == null || gherkinContent == null || requestDataList == null || karateScript == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        List<String> errors = new ArrayList<>();
        // Get title from Swagger info
        String swaggerTitle = openAPI.getInfo().getTitle();
        // Background declaration if it is a background script
        if (isBackground) {
            // Feature declaration outside the loop
            karateScript.append(String.format(KarateSyntaxParam.FEATURE, swaggerTitle));
            karateScript.append(KarateSyntaxParam.BACKGROUND);
            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.URL, openAPI.getServers().get(0).getUrl())); // Adding the base URL
        }
        // Loop through paths
        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            // Loop through operations for each path
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                String gherkinTag = new GherkinTag().getGherkinTag(gherkinContent, httpMethod, operation, errors, path);
                if ((isBackground && gherkinTag != null && gherkinTag.equals("#session")) || (!isBackground && gherkinTag != null && !gherkinTag.equals("#session"))) {
                    if (isBackground) {
                        karateScript.append(String.format(KarateSyntaxParam.PROMPT, operation.getDescription()));
                        karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.PATH, path));
                    } else {
                        karateScript.append(String.format(KarateSyntaxParam.SCENARIO, operation.getDescription()));
                        karateScript.append(String.format(KarateSyntaxParam.GIVEN + KarateSyntaxParam.PATH, path));
                    }
                    handleOperationParameters(operation, requestDataList, path, karateScript, isBackground);
                    operation.getResponses().forEach((statusCode, response) -> {
                        if (isBackground) {
                            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.METHOD, httpMethod));
                            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.STATUS, statusCode));
                            if (openAPI.getSecurity() != null) {
                                karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_TOKEN);
                                karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.HEADER_TOKEN);
                            }
                        } else {
                            karateScript.append(String.format(KarateSyntaxParam.WHEN + KarateSyntaxParam.STATUS, httpMethod));
                            karateScript.append(String.format(KarateSyntaxParam.THEN + KarateSyntaxParam.STATUS, statusCode));
                        }
                    });
                }
            });
        }

        if (!errors.isEmpty()) {
            System.out.println("Errors encountered:");
            errors.forEach(System.out::println);
        }
    }

    private void handleOperationParameters(Operation operation, List<Map<String, String>> requestDataList, String path, StringBuilder karateScript, boolean isBackground) {
        if (operation.getParameters() != null) {
            for (Map<String, String> requestData : requestDataList) {
                String requestDataPath = requestData.get("path");
                String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
                String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
                if (normalizedRequestPath.equals(normalizedPath)) {
                    String requestText = requestData.get("request_text");
                    // 检查参数的 'in' 属性是否为 'query'，如果是，则解析成 'And param'，否则解析成 'And request'
                    if ("query".equals(operation.getParameters().get(0).getIn())) {
                        handleQueryParams(karateScript, requestText, isBackground);
                    } else {
                        handleJsonRequest(karateScript, requestText, isBackground);
                    }
                    break;
                }
            }
        }
    }

    private void handleQueryParams(StringBuilder karateScript, String requestText, boolean isBackground) {
        String[] keyValuePairs = requestText.split("&");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                if (isBackground) {
                    karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.PARAM, parts[0], parts[1]));
                } else {
                    karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.PARAM, parts[0], parts[1]));
                }
            }
        }
    }

    private void handleJsonRequest(StringBuilder karateScript, String requestText, boolean isBackground) {
        ObjectNode jsonRequest = new ObjectMapper().createObjectNode();
        String[] keyValuePairs = requestText.split("&");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                jsonRequest.put(parts[0], parts[1]);
            }
        }
        if (isBackground) {
            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.REQUEST, jsonRequest.toString()));
        } else {
            karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.REQUEST, jsonRequest.toString()));
        }
    }
}
