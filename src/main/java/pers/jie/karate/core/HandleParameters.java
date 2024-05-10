package pers.jie.karate.core;

import com.google.gson.*;
import pers.jie.karate.param.KarateSyntaxParam;

import java.util.*;

public class HandleParameters {

    public void handleOperationParameters(Map<String, String> requestData, StringBuilder karateScript, boolean isBackground) {
        String requestText = requestData.get("request_text");
        boolean isJson = requestText.matches("^\\s*(\\{.*}|\\[.*])\\s*$");
        if (isJson) {
            handleJsonRequest(requestText, karateScript, isBackground);
        } else {
            handleFormUrlEncodedRequest(requestText, karateScript, isBackground);
        }
    }

    private void handleJsonRequest(String requestText, StringBuilder karateScript, boolean isBackground) {
        if (isBackground) {
            karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.REQUEST, getFirstData(requestText)));
        } else {
            karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.REQUEST, convertToDesiredFormat(requestText)));
        }
    }

    private void handleFormUrlEncodedRequest(String requestText, StringBuilder karateScript, boolean isBackground) {
        String[] lines = requestText.split("\n");
        String[] keyValuePairs = lines[0].split("&");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                if (isBackground) {
                    karateScript.append(String.format(KarateSyntaxParam.BACKGROUND_TITLE + KarateSyntaxParam.PARAM, parts[0], parts[1]));
                } else {
                    karateScript.append(String.format(KarateSyntaxParam.AND + KarateSyntaxParam.TABLES_PARAM, parts[0], parts[0]));
                }
            }
        }
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
        for (Map.Entry<String, JsonElement> entry : firstData.entrySet()) {
            String key = entry.getKey();
            result.addProperty(key, "<" + key + ">");
        }
        return result.toString();
    }
}
