package pers.jie.karate.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CombinationTesting {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "src/test/java/pers/jie/karate/api/karate-script.feature";  // 請輸入您的輸入文件路徑
        String outputFilePath = "src/test/java/pers/jie/karate/api/output.feature";  // 請輸入您的輸出文件路徑

        // 讀取 Feature 文件內容
        String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        System.out.println("Reading feature file content:");
        System.out.println(content);

        // 解析 Scenario Outline 和 Examples
        List<String> scenarios = parseFeatureFile(content);
        System.out.println("Parsed scenarios:");
        scenarios.forEach(System.out::println);

        int t = 2; // 您可以设置t为所需的组合级别，例如2代表Pairwise测试

        List<String> newExamples = scenarios.stream().map(scenario -> {
            String exampleText = scenario.split("Examples:\n", 2)[1];
            Map<String, List<String>> examples = parseExamples(exampleText);
            List<Map<String, String>> combinations = generateTWayCombinations(examples, t);
            return generateExampleTable(examples.keySet().stream().toList(), combinations);
        }).collect(Collectors.toList());

        // 更新 Feature 文件內容
        String updatedContent = updateFeatureFile(content, scenarios, newExamples);
        System.out.println("Updated feature file content:");
        System.out.println(updatedContent);

        // 寫入新的 Feature 文件
        Files.write(Paths.get(outputFilePath), updatedContent.getBytes());
    }

    // 解析 Feature 文件中的 Scenario Outline 和 Examples
    public static List<String> parseFeatureFile(String content) {
        Pattern pattern = Pattern.compile("(Scenario Outline:.*?Examples:\\n(\\s*\\|.*?\\|.*?\\n)+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        List<String> scenarios = new ArrayList<>();

        while (matcher.find()) {
            scenarios.add(matcher.group(1));
        }
        return scenarios;
    }

    // 解析 Example 表格
    public static Map<String, List<String>> parseExamples(String exampleText) {
        String[] lines = exampleText.trim().split("\\n");
        String[] headers = lines[0].split("\\|");
        headers = Arrays.stream(headers).map(String::trim).filter(h -> !h.isEmpty()).toArray(String[]::new);

        Map<String, List<String>> examples = new LinkedHashMap<>();
        for (String header : headers) {
            examples.put(header, new ArrayList<>());
        }

        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split("\\|");
            for (int j = 0; j < headers.length; j++) {
                examples.get(headers[j]).add(values[j + 1].trim());
            }
        }

        return examples;
    }

    // 生成t-way组合测试的 Examples
    public static List<Map<String, String>> generateTWayCombinations(Map<String, List<String>> examples, int t) {
        List<Map<String, String>> combinations = new ArrayList<>();
        List<String> keys = new ArrayList<>(examples.keySet());

        // 生成键的组合
        Set<Set<String>> keyCombinations = Sets.combinations(new LinkedHashSet<>(keys), t);

        for (Set<String> keySet : keyCombinations) {
            List<Set<String>> valuesList = new ArrayList<>();
            for (String key : keySet) {
                valuesList.add(new HashSet<>(examples.get(key)));
            }
            Set<List<String>> valueCombinations = Sets.cartesianProduct(valuesList);

            for (List<String> valueList : valueCombinations) {
                Map<String, String> combination = new LinkedHashMap<>();
                for (String key : keys) {
                    if (keySet.contains(key)) {
                        combination.put(key, valueList.get(new ArrayList<>(keySet).indexOf(key)));
                    } else {
                        combination.put(key, examples.get(key).get(0)); // 填充其他参数的默认值
                    }
                }
                combinations.add(combination);
            }
        }

        return combinations;
    }

    // 生成新的 Example 表格
    public static String generateExampleTable(List<String> headers, List<Map<String, String>> examples) {
        String headerRow = String.join(" | ", headers);
        List<String> rows = new ArrayList<>();
        rows.add(headerRow);

        for (Map<String, String> example : examples) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                row.add(example.getOrDefault(header, ""));
            }
            rows.add(String.join(" | ", row));
        }

        return rows.stream().map(row -> "      | " + row + " |").collect(Collectors.joining("\n"));
    }

    // 更新 feature 文件
    public static String updateFeatureFile(String content, List<String> scenarios, List<String> newExamples) {
        String updatedContent = content;
        for (int i = 0; i < scenarios.size(); i++) {
            String scenario = scenarios.get(i);
            String newExampleTable = newExamples.get(i);
            Matcher matcher = Pattern.compile("Examples:\\n(\\s*\\|.*?\\|.*?\\n)+", Pattern.DOTALL).matcher(scenario);
            while (matcher.find()) {
                String oldExampleSection = matcher.group(0);
                String newExampleSection = "Examples:\n" + newExampleTable + "\n";
                scenario = scenario.replace(oldExampleSection, newExampleSection);
            }
            updatedContent = updatedContent.replace(scenarios.get(i), scenario);
        }
        return updatedContent;
    }
}
