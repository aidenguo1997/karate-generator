package pers.jie.karate.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import pers.jie.karate.param.FilePathParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class GeneratorKarateFile {
    private final GeneratorScript generatorScript;

    public GeneratorKarateFile(GeneratorScript generatorScript) {
        this.generatorScript = generatorScript;
    }

    public void convertGherkinToKarate() {
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

    private StringBuilder generateKarateScript(OpenAPI openAPI, String gherkinContent, List<Map<String, String>> requestDataList) {
        StringBuilder karateScript = new StringBuilder();
        karateScript.append(generatorScript.backgroundKarateScript(openAPI, gherkinContent, requestDataList));
        karateScript.append(generatorScript.scenarioKarateScript(openAPI, gherkinContent, requestDataList));
        return karateScript;
    }

    private void writeKarateScriptToFile(StringBuilder karateScript) throws IOException {
        Path path = Path.of(FilePathParam.KARATE_SCRIPT_PATH);
        Files.writeString(path, karateScript.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Karate script written to: " + path.toAbsolutePath());
        System.out.println(karateScript);
    }
}
