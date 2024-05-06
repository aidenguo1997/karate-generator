package pers.jie.karate.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

public class ExamplesTableConverter {

    public static String convertToExamplesTable(String data) {
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
        examplesTable.append("|");
        for (String key : keys) {
            examplesTable.append(" ").append(key).append(" |");
        }
        examplesTable.append("\n");

        // 添加數據行
        for (String line : lines) {
            examplesTable.append("|");
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

    public static void main(String[] args) {
        String data = "user_name=zx123&user_password=zxc123321&user_nickname=ee&user_birthday=2024-05-18&user_gender=0&user_address=110101\nuser_name=qa123&user_password=zxc123321&user_nickname=ww&user_birthday=2024-05-09&user_gender=0&user_address=110101";
        String examplesTable = convertToExamplesTable(data);
        System.out.println(examplesTable);
    }
}

