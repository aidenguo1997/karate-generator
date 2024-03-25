package pers.jie.karate.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class GeneratorKarateFile {
    private static final String KARATE_SCRIPT_PATH = "src/test/java/pers/jie/karate/api/karate-script.feature";

    public static void main(String[] args) {
        convertGherkinToKarate("src/test/resources/Features/2.feature", "src/main/resources/Swaggers/2.json", "src/main/resources/Data/2.json");
    }

    public static void convertGherkinToKarate(String gherkinPath, String swaggerPath, String jsonFilePath) {
        try {
            String gherkinContent = readFileContent(gherkinPath);
            OpenAPI openAPI = parseSwagger(swaggerPath);
            List<Map<String, String>> requestDataList = readRequestData(jsonFilePath);
            StringBuilder karateScript = generateKarateScript(openAPI, gherkinContent, requestDataList);
            writeKarateScriptToFile(karateScript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    private static OpenAPI parseSwagger(String swaggerPath) {
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(swaggerPath, null, null);
        return parseResult.getOpenAPI();
    }

    private static List<Map<String, String>> readRequestData(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(jsonFilePath), new TypeReference<>() {
        });
    }

    private static StringBuilder generateKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        karateScript.append(GeneratorBackgroundScript.generateBackgroundKarateScript(openAPI, gherkinContent, requestDataList));
        karateScript.append(GeneratorScenarioScript.generateScenarioKarateScript(openAPI, gherkinContent, requestDataList));
        return karateScript;
    }

    private static void writeKarateScriptToFile(StringBuilder karateScript) throws IOException {
        Path path = Path.of(KARATE_SCRIPT_PATH);
        Files.writeString(path, karateScript.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Karate script written to: " + path.toAbsolutePath());
        System.out.println(karateScript);
    }
}

