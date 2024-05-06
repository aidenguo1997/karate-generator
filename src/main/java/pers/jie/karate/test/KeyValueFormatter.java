package pers.jie.karate.test;

import java.util.ArrayList;
import java.util.List;

public class KeyValueFormatter {

    public static List<String> formatKeys(String data) {
        List<String> formattedKeys = new ArrayList<>();

        // 分割字符串为行
        String[] lines = data.split("\n");

        // 处理第一行数据
        String[] keyValuePairs = lines[0].split("&");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                formattedKeys.add("'<" + parts[0] + ">'");
            }
        }

        return formattedKeys;
    }

    public static void main(String[] args) {
        String data = "user_name=zx123&user_password=zxc123321&user_nickname=ee&user_birthday=2024-05-18&user_gender=0&user_address=110101\nuser_name=qa123&user_password=zxc123321&user_nickname=ww&user_birthday=2024-05-09&user_gender=0&user_address=110101";
        List<String> formattedKeys = formatKeys(data);
        for (String key : formattedKeys) {
            System.out.println(key);
        }
    }
}
