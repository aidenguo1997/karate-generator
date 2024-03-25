package pers.jie.karate.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class GeneratorKarateFile {
    public static void main(String[] args) {
        convertGherkinToKarate("src/test/resources/Features/2.feature", "src/main/resources/Swaggers/2.json", "src/main/resources/Data/2.json");
    }

    public static void convertGherkinToKarate(String gherkinPath, String swaggerPath, String jsonFilePath) {
        try {
            // Parse Gherkin content
            String gherkinContent = new String(Files.readAllBytes(Path.of(gherkinPath)));
            // Parse Swagger JSON using io.swagger.v3
            SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(swaggerPath, null, null);
            OpenAPI openAPI = parseResult.getOpenAPI();
            // Read JSON file to get request data
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> requestDataList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, String>>>() {
            });
            // Generate Karate feature file content
            StringBuilder karateBackgroundScript = new GeneratorBackgroundScript().generateBackgroundKarateScript(openAPI, gherkinContent, requestDataList);
            StringBuilder karateScenarioScript = new GeneratorScenarioScript().generateScenarioKarateScript(openAPI, gherkinContent, requestDataList);
            karateBackgroundScript.append(karateScenarioScript);
            String karateScript = karateBackgroundScript.toString();
            writeKarateScriptToFile(karateScript);
            System.out.println("Generated Karate Script:\n" + karateScript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeKarateScriptToFile(String karateScript) {
        try {
            Path path = Path.of("src/test/java/pers/jie/karate/api/karate-script.feature");
            Files.write(path, karateScript.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Karate script written to: " + path.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

