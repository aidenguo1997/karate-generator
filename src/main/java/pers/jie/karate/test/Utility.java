package pers.jie.karate.test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Utility {

    public static String convertToDesiredFormat(String jsonDataString) {
        // 解析 JSON 字符串为列表
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonDataString, JsonArray.class);

        // 创建结果 JSON 对象
        JsonObject result = new JsonObject();

        // 遍历列表，提取数据并构建结果对象
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject data = jsonArray.get(i).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getKey();
                result.addProperty(key, "<" + value + ">");
            }
            // 只需要第一组数据
            break;
        }

        return result.toString();
    }

    public static void main(String[] args) {
        // 示例数据
        String jsonDataString = "[{\"name\": \"wds\", \"nonce\": \"11296274215075840\"}, {\"name\": \"CRT\", \"nonce\": \"11296275445317632\"}, {\"name\": \"TES\", \"nonce\": \"11296288089571328\"}, {\"name\": \"EYB\", \"nonce\": \"11296294094241792\"}, {\"name\": \"WTR\", \"nonce\": \"11296331297456128\"}]";
        String result = convertToDesiredFormat(jsonDataString);
        System.out.println(result);
    }
}

