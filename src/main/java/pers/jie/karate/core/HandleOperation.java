package pers.jie.karate.core;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Schema;
import pers.jie.karate.param.KarateSyntaxParam;
import pers.jie.karate.tag.GherkinTag;

import java.util.List;
import java.util.Map;

public class HandleOperation {

    private final GherkinTag gherkinTag;

    public HandleOperation(GherkinTag gherkinTag) {
        this.gherkinTag = gherkinTag;
    }

    public void handleOperation(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, boolean isBackground, StringBuilder karateScript, List<String> errors) {
        // 参数验证
        assertParametersNotNull(openAPI, gherkinContent, requestDataList, karateScript);
        // Get title from Swagger info
        String swaggerTitle = openAPI.getInfo().getTitle();
        // Background declaration if it is a background script
        if (isBackground) {
            // Feature declaration outside the loop
            karateScript.append(String.format(KarateSyntaxParam.FEATURE, swaggerTitle));
            karateScript.append(KarateSyntaxParam.BACKGROUND);
            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.URL, openAPI.getServers().get(0).getUrl())); // Adding the base URL
            karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.CHARSET);
        }
        // Loop through paths
        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            // Loop through operations for each path
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                String tag = gherkinTag.getGherkinTag(gherkinContent, httpMethod, operation, errors, path);
                boolean isSession = gherkinTag.isSessionTag();
                if ((isBackground == isSession) && tag != null) {
                    if (isBackground) {
                        karateScript.append(String.format(KarateSyntaxParam.PROMPT, operation.getDescription().replaceAll("\n", "")));
                        karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.PATH, path));
                    } else {
                        karateScript.append(String.format(KarateSyntaxParam.SCENARIO, operation.getDescription().replaceAll("\n", "")));
                        karateScript.append(String.format(KarateSyntaxParam.GIVEN + KarateSyntaxParam.PATH, path));
                    }
                    handleOperationParameters(operation, requestDataList, path, karateScript, isBackground);
                    operation.getResponses().forEach((statusCode, response) -> {
                        int statusCodeValue = Integer.parseInt(statusCode);
                        if (statusCodeValue >= 200 && statusCodeValue < 300) {
                            if (isBackground) {
                                karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.METHOD, httpMethod));
                                karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.STATUS, statusCode));
                                boolean isSecurity = hasSecurity(openAPI);
                                if (isSecurity) {
                                    boolean isBodyToken = isTokenPropertyExists(openAPI);
                                    if (isBodyToken) {
                                        karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_BODY_TOKEN);
                                    } else {
                                        karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_HEADERS_TOKEN);
                                    }
                                    karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.HEADER_TOKEN);
                                }
                            } else {
                                karateScript.append(String.format(KarateSyntaxParam.WHEN + KarateSyntaxParam.METHOD, httpMethod));
                                karateScript.append(String.format(KarateSyntaxParam.THEN + KarateSyntaxParam.STATUS, statusCode));
                            }
                        }

                    });
                }
            });
        }
    }

    private void handleOperationParameters(Operation operation, List<Map<String, String>> requestDataList, String path, StringBuilder karateScript, boolean isBackground) {
        if (operation.getParameters() != null || operation.getRequestBody() != null) {
            for (Map<String, String> requestData : requestDataList) {
                String requestDataPath = requestData.get("path");
                String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
                String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
                if (normalizedRequestPath.equals(normalizedPath)) {
                    String requestText = requestData.get("request_text");
                    boolean isJson = requestText.matches("^\\s*(\\{.*}|\\[.*])\\s*$");
                    if (isJson) {
                        if (isBackground) {
                            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.REQUEST, requestText));
                        } else {
                            karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.REQUEST, requestText));
                        }
                    } else {
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
                    break;
                }
            }
        }
    }

    private boolean hasSecurity(OpenAPI openAPI) {
        // 首先檢查全局的安全屬性是否存在
        return openAPI.getComponents().getSecuritySchemes() != null;
    }

    private boolean isTokenPropertyExists(OpenAPI openAPI) {
        // 獲取 components
        Components components = openAPI.getComponents();
        if (components.getSchemas() != null) {
            // 遍歷所有的 schemas
            for (Schema<?> schema : components.getSchemas().values()) {
                // 查找具有 token 屬性的 schema
                if (schema.getProperties() != null && schema.getProperties().containsKey("token")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void assertParametersNotNull(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, StringBuilder karateScript) {
        assert openAPI != null : "OpenAPI cannot be null";
        assert gherkinContent != null : "Gherkin content cannot be null";
        assert requestDataList != null : "Request data list cannot be null";
        assert karateScript != null : "Karate script cannot be null";
    }
}
