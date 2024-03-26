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

import static pers.jie.karate.generator.FilePath.*;

public class GeneratorKarateFile {
    public static void main(String[] args) {
        convertGherkinToKarate();
    }

    private static void convertGherkinToKarate() {
        try {
            String gherkinContent = readFileContent();
            OpenAPI openAPI = parseSwagger();
            List<Map<String, String>> requestDataList = readRequestData();
            StringBuilder karateScript = generateKarateScript(openAPI, gherkinContent, requestDataList);
            writeKarateScriptToFile(karateScript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent() throws IOException {
        return Files.readString(Path.of(GHERKIN_PATH));
    }

    private static OpenAPI parseSwagger() {
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(SWAGGER_PATH, null, null);
        return parseResult.getOpenAPI();
    }

    private static List<Map<String, String>> readRequestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(REQUEST_DATA_PATH), new TypeReference<>() {
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

