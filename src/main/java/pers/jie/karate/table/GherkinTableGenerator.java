package pers.jie.karate.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GherkinTableGenerator {

    public static void main(String[] args) {
        // 从外部 JSON 文件加载测试数据
        List<Map<String, String>> requestDataList = loadTestData();

        // 按路径和方法分组请求
        Map<String, List<String>> groupedRequests = groupRequests(requestDataList);

        // 生成 Gherkin 表格
        for (Map.Entry<String, List<String>> entry : groupedRequests.entrySet()) {
            String path = entry.getKey().split("\\|")[0]; // 提取路径信息
            String gherkinTable = generateGherkinTable(entry.getValue());
            //System.out.println(entry.getValue());
            System.out.println("@" + path); // 添加路径信息到标签上方
            System.out.println(gherkinTable);
        }
    }

    private static Map<String, List<String>> groupRequests(List<Map<String, String>> requestDataList) {
        Map<String, List<String>> groupedRequests = new HashMap<>();
        for (Map<String, String> requestData : requestDataList) {
            String path = requestData.get("path");
            String method = requestData.get("method");
            String requestText = requestData.get("request_text");
            String key = path + "|" + method;
            groupedRequests.computeIfAbsent(key, k -> new ArrayList<>()).add(requestText);
        }
        System.out.println(groupedRequests);
        return groupedRequests;
    }

    private static String generateGherkinTable(List<String> requestTexts) {
        StringBuilder tableBuilder = new StringBuilder();

        // 生成 Gherkin 表格头部
        Map<String, String> firstRequestData = parseRequestText(requestTexts.get(0));
        for (String header : firstRequestData.keySet()) {
            tableBuilder.append("| ").append(header).append(" ");
        }
        tableBuilder.append("|").append("\n");

        // 生成 Gherkin 表格数据
        for (String requestText : requestTexts) {
            Map<String, String> requestData = parseRequestText(requestText);
            for (String value : requestData.values()) {
                tableBuilder.append("| ").append(value).append(" ");
            }
            tableBuilder.append("|").append("\n");
        }

        return tableBuilder.toString();
    }

    private static Map<String, String> parseRequestText(String requestText) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 判断请求文本的格式是 JSON 还是键值对
            if (requestText.startsWith("{") && requestText.endsWith("}")) {
                // 如果是 JSON 格式，则直接解析
                return objectMapper.readValue(requestText, new TypeReference<>() {});
            } else {
                // 如果是键值对格式，则调用 parseKeyValueText 方法进行解析
                return parseKeyValueText(requestText);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private static Map<String, String> parseKeyValueText(String text) throws IOException {
        // 将键值对字符串解析为 Map
        String[] keyValuePairs = text.split("&");
        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }


    private static List<Map<String, String>> loadTestData() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File("src/main/resources/Data/3.json"), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

