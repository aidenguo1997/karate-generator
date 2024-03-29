package pers.jie.karate.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import pers.jie.karate.param.FilePathParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class GeneratorKarateFile {
    private final GeneratorScript generatorScript;

    public GeneratorKarateFile(GeneratorScript generatorScript) {
        this.generatorScript = generatorScript;
    }

    public void convertGherkinToKarate(List<String> errors) {
        try {
            String gherkinContent = readFileContent();
            OpenAPI openAPI = parseSwagger();
            List<Map<String, String>> requestDataList = readRequestData();
            StringBuilder karateScript = generateKarateScript(openAPI, gherkinContent, requestDataList, errors);
            writeKarateScriptToFile(karateScript);
        } catch (IOException e) {
            errors.add("Error occurred while reading file: " + e.getMessage());
        } catch (Exception e) {
            errors.add("Unexpected error occurred: " + e.getMessage());
        }
    }

    private String readFileContent() throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(FilePathParam.GHERKIN_PATH), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private OpenAPI parseSwagger() {
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(FilePathParam.SWAGGER_PATH, null, null);
        return parseResult.getOpenAPI();
    }

    private List<Map<String, String>> readRequestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(FilePathParam.REQUEST_DATA_PATH), new TypeReference<>() {
        });
    }

    private StringBuilder generateKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList, List<String> errors) {
        StringBuilder karateScript = new StringBuilder();
        generatorScript.backgroundKarateScript(openAPI, gherkinContent, requestDataList, errors, karateScript);
        generatorScript.scenarioKarateScript(openAPI, gherkinContent, requestDataList, errors, karateScript);
        return karateScript;
    }

    private void writeKarateScriptToFile(StringBuilder karateScript) throws IOException {
        Path path = Path.of(FilePathParam.KARATE_SCRIPT_PATH);
        Files.writeString(path, karateScript.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Karate script written to: " + path.toAbsolutePath());
        System.out.println(karateScript);
    }
}
