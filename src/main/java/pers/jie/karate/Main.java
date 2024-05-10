package pers.jie.karate;

import pers.jie.karate.core.HandleDataTables;
import pers.jie.karate.core.HandleOperation;
import pers.jie.karate.core.HandleParameters;
import pers.jie.karate.core.HandleRequestData;
import pers.jie.karate.generator.GeneratorKarateFile;
import pers.jie.karate.generator.GeneratorScript;
import pers.jie.karate.tag.GherkinTag;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GherkinTag gherkinTag = new GherkinTag();
        HandleParameters parameters = new HandleParameters();
        HandleRequestData requestData = new HandleRequestData();
        HandleDataTables dataTables = new HandleDataTables(requestData);
        HandleOperation handleOperation = new HandleOperation(gherkinTag, parameters, requestData, dataTables);
        GeneratorScript generatorScript = new GeneratorScript(handleOperation);
        GeneratorKarateFile generatorKarateFile = new GeneratorKarateFile(generatorScript);
        List<String> errors = new ArrayList<>();
        generatorKarateFile.convertGherkinToKarate(errors);
        if (!errors.isEmpty()) {
            System.out.println("Errors encountered:");
            errors.forEach(System.out::println);
        }
    }
}
