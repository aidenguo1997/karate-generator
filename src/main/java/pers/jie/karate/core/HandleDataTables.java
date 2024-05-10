package pers.jie.karate.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class HandleDataTables {
    private final HandleRequestData requestData;

    public HandleDataTables(HandleRequestData requestData) {
        this.requestData = requestData;
    }

    public String handleOperationParametersTable(List<Map<String, String>> requestDataList, String path) {
        Map<String, String> request = requestData.findMatchingRequestData(requestDataList, path);
        if (request != null) {
            String requestText = request.get("request_text");
            boolean isJson = requestText.matches("^\\s*(\\{.*}|\\[.*])\\s*$");
            if (isJson) {
                Gson gson = new Gson();
                List<Map<String, String>> jsonData = gson.fromJson(requestText, new TypeToken<List<Map<String, String>>>() {
                }.getType());
                return convertToJsonTable(jsonData);
            } else {
                return convertToFormUrlEncodedTable(requestText);
            }
        }
        return null;
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

    private String convertToFormUrlEncodedTable(String data) {
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
}
