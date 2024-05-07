package pers.jie.karate.core;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.*;
import com.google.gson.*;

import pers.jie.karate.param.KarateSyntaxParam;
import pers.jie.karate.tag.GherkinTag;

import java.util.*;

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
                                    boolean isBodyToken = isBodyToken(openAPI);
                                    if (isBodyToken) {
                                        karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_BODY_TOKEN).append(bodyTokenPath(openAPI));
                                    } else {
                                        karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.DEF_HEADERS_TOKEN);
                                    }
                                    karateScript.append(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.HEADER_TOKEN);
                                }
                            } else {
                                karateScript.append(String.format(KarateSyntaxParam.WHEN + KarateSyntaxParam.METHOD, httpMethod));
                                karateScript.append(String.format(KarateSyntaxParam.THEN + KarateSyntaxParam.STATUS, statusCode));
                                karateScript.append(handleOperationParametersTable(operation, requestDataList, path));
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
                            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.REQUEST, getFirstData(requestText)));
                        } else {
                            karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.REQUEST, convertToDesiredFormat(requestText)));
                        }
                    } else {

                        String[] lines = requestText.split("\n");
                        String[] keyValuePairs = lines[0].split("&");
                        for (String pair : keyValuePairs) {
                            String[] parts = pair.split("=");
                            System.out.println(parts[0] + parts[1]);
                            if (parts.length == 2) {
                                if (isBackground) {
                                    karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.PARAM, parts[0], parts[1]));
                                } else {
                                    karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.PARAM, parts[0], parts[0]));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private String handleOperationParametersTable(Operation operation, List<Map<String, String>> requestDataList, String path) {
        if (operation.getParameters() != null || operation.getRequestBody() != null) {
            for (Map<String, String> requestData : requestDataList) {
                String requestDataPath = requestData.get("path");
                String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
                String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
                if (normalizedRequestPath.equals(normalizedPath)) {
                    String requestText = requestData.get("request_text");
                    boolean isJson = requestText.matches("^\\s*(\\{.*}|\\[.*])\\s*$");
                    if (isJson) {
                        Gson gson = new Gson();
                        List jsonData = gson.fromJson(requestText, List.class);
                        return convertToJsonTable(jsonData);
                    } else {
                        return convertToExamplesTable(requestText);
                    }
                }
            }
        }
        return null;
    }

    private JsonObject getFirstData(String jsonDataString) {
        // 解析 JSON 字符串为列表
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonDataString, JsonArray.class);
        // 取第一个对象，构建结果对象
        return jsonArray.get(0).getAsJsonObject();
    }

    private String convertToDesiredFormat(String jsonDataString) {
        // 创建结果 JSON 对象
        JsonObject result = new JsonObject();
        // 取第一个对象，构建结果对象
        JsonObject firstData = getFirstData(jsonDataString);
        //System.out.println(firstData);
        for (Map.Entry<String, JsonElement> entry : firstData.entrySet()) {
            String key = entry.getKey();
            result.addProperty(key, "<" + key + ">");
        }
        return result.toString();
    }

    private String convertToJsonTable(List<Map<String, String>> jsonData) {
        StringBuilder table = new StringBuilder("    Examples:\n");
        if (jsonData != null && !jsonData.isEmpty()) {
            // Get the keys from the first map
            Map<String, String> firstMap = jsonData.get(0);
            String[] keys = firstMap.keySet().toArray(new String[0]);

            // Append table header
            table.append("      | ");
            for (String key : keys) {
                table.append(key).append(" | ");
            }
            table.append("\n");

            // Append table data
            for (Map<String, String> map : jsonData) {
                table.append("      | ");
                for (String key : keys) {
                    table.append(map.get(key)).append(" | ");
                }
                table.append("\n");
            }
        }
        return table.toString();
    }

    private String convertToExamplesTable(String data) {
        // 分割字符串为行
        String[] lines = data.split("\n");

        // 使用 Set 來確保鍵的唯一性
        Set<String> keysSet = new HashSet<>();

        // 遍歷每一行，獲取所有鍵
        for (String line : lines) {
            String[] keyValuePairs = line.split("&");
            for (String pair : keyValuePairs) {
                String[] parts = pair.split("=");
                keysSet.add(parts[0]); // 添加到 Set 中
            }
        }

        // 將 Set 轉換為列表並按字母順序排序
        List<String> keys = new ArrayList<>(keysSet);
        Collections.sort(keys);

        // 构建 Examples 表格
        StringBuilder examplesTable = new StringBuilder();

        // 添加表頭
        examplesTable.append("    Examples:\n");
        examplesTable.append("      |");
        for (String key : keys) {
            examplesTable.append(" ").append(key).append(" |");
        }
        examplesTable.append("\n");

        // 添加數據行
        for (String line : lines) {
            examplesTable.append("      |");
            String[] keyValuePairs = line.split("&");
            for (String key : keys) {
                String value = ""; // 初始化為空字符串
                // 遍歷每個鍵值對，找到對應的值
                for (String pair : keyValuePairs) {
                    String[] parts = pair.split("=");
                    if (parts[0].equals(key)) {
                        value = parts[1];
                        break;
                    }
                }
                examplesTable.append(" ").append(value).append(" |");
            }
            examplesTable.append("\n");
        }

        return examplesTable.toString();
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
