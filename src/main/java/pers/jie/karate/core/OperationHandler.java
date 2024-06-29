package pers.jie.karate.core;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.*;

import pers.jie.karate.param.KarateSyntaxParam;
import pers.jie.karate.tag.GherkinTag;

import java.util.*;

public class OperationHandler {

    private final GherkinTag gherkinTag;
    private final ParametersHandler parameters;
    private final RequestDataHandler requestData;
    private final DataTablesHandler dataTables;

    public OperationHandler(GherkinTag gherkinTag, ParametersHandler parameters, RequestDataHandler requestData, DataTablesHandler dataTables) {
        this.gherkinTag = gherkinTag;
        this.parameters = parameters;
        this.requestData = requestData;
        this.dataTables = dataTables;
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
                    handleOperationDetails(openAPI, operation, requestDataList, path, httpMethod, karateScript, isBackground);
                }
            });
        }
    }

    private void handleOperationDetails(OpenAPI openAPI, Operation operation, List<Map<String, String>> requestDataList, String path, PathItem.HttpMethod httpMethod, StringBuilder karateScript, boolean isBackground) {
        if (operation.getParameters() != null || operation.getRequestBody() != null) {
            Map<String, String> request = requestData.findMatchingRequestData(requestDataList, path, httpMethod);
            if (request != null) {
                parameters.handleOperationParameters(request, karateScript, isBackground);
                handleResponseStatus(openAPI, operation, path, requestDataList, karateScript, httpMethod, isBackground);
            }
        }
    }

    private void handleResponseStatus(OpenAPI openAPI, Operation operation, String path, List<Map<String, String>> requestDataList, StringBuilder karateScript, PathItem.HttpMethod httpMethod, boolean isBackground) {
        operation.getResponses().forEach((statusCode, response) -> {
            int statusCodeValue = Integer.parseInt(statusCode);
            if (statusCodeValue >= 200 && statusCodeValue < 300) {
                if (isBackground) {
                    karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.METHOD, httpMethod));
                    karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.STATUS, statusCode));
                    boolean isSecurity = hasSecurity(openAPI);
                    if (isSecurity) {
                        boolean isBodyToken = isBodyToken(openAPI);
                        if (isBodyToken) {
                            karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_BODY_TOKEN).append(bodyTokenPath(openAPI)).append(KarateSyntaxParam.NEWLINE);
                        } else {
                            karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_HEADERS_TOKEN);
                        }
                        karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.HEADER_TOKEN);
                    }
                } else {
                    karateScript.append(String.format(KarateSyntaxParam.WHEN + KarateSyntaxParam.METHOD, httpMethod));
                    karateScript.append(String.format(KarateSyntaxParam.THEN + KarateSyntaxParam.STATUS, statusCode));
                    karateScript.append(dataTables.handleOperationParametersTable(requestDataList, path, httpMethod));
                }
            }
        });
    }

    private boolean hasSecurity(OpenAPI openAPI) {
        // 检查每个操作的安全要求
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            PathItem pathItem = entry.getValue();
            for (Operation operation : pathItem.readOperations()) {
                if (hasSecurityRequirement(operation.getSecurity())) {
                    return true;
                }
            }
        }
        // 检查全局的安全要求
        return hasSecurityRequirement(openAPI.getSecurity());
    }

    private static boolean hasSecurityRequirement(List<SecurityRequirement> securityRequirements) {
        return securityRequirements != null && !securityRequirements.isEmpty();
    }

    private <T> T getExtensionValue(OpenAPI openAPI, String extensionName, Class<T> valueType) {
        Map<String, Object> extensions = openAPI.getInfo().getExtensions();
        if (extensions != null && extensions.containsKey(extensionName)) {
            Object value = extensions.get(extensionName);
            if (valueType.isInstance(value)) {
                return valueType.cast(value);
            }
        }
        return null;
    }

    private boolean isBodyToken(OpenAPI openAPI) {
        return Boolean.TRUE.equals(getExtensionValue(openAPI, "x-has-body-token", Boolean.class));
    }

    private String bodyTokenPath(OpenAPI openAPI) {
        String path = getExtensionValue(openAPI, "x-body-token-path", String.class);
        return path != null ? path.replace("$", "") : null;
    }

    private void assertParametersNotNull(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, StringBuilder karateScript) {
        assert openAPI != null : "OpenAPI cannot be null";
        assert gherkinContent != null : "Gherkin content cannot be null";
        assert requestDataList != null : "Request data list cannot be null";
        assert karateScript != null : "Karate script cannot be null";
    }
}
