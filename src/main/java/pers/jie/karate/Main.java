package pers.jie.karate;

import pers.jie.karate.core.DataTablesHandler;
import pers.jie.karate.core.OperationHandler;
import pers.jie.karate.core.ParametersHandler;
import pers.jie.karate.core.RequestDataHandler;
import pers.jie.karate.generator.KarateFileGenerator;
import pers.jie.karate.generator.KarateElementGenerator;
import pers.jie.karate.tag.GherkinTag;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GherkinTag gherkinTag = new GherkinTag();
        ParametersHandler parameters = new ParametersHandler();
        RequestDataHandler requestData = new RequestDataHandler();
        DataTablesHandler dataTables = new DataTablesHandler(requestData);
        OperationHandler operationHandler = new OperationHandler(gherkinTag, parameters, requestData, dataTables);
        KarateElementGenerator karateElementGenerator = new KarateElementGenerator(operationHandler);
        KarateFileGenerator karateFileGenerator = new KarateFileGenerator(karateElementGenerator);
        List<String> errors = new ArrayList<>();
        karateFileGenerator.convertGherkinToKarate(errors);
        if (!errors.isEmpty()) {
            System.out.println("Errors encountered:");
            errors.forEach(System.out::println);
        }
    }
}
