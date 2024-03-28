package pers.jie.karate;

import pers.jie.karate.core.HandleOperation;
import pers.jie.karate.generator.GeneratorKarateFile;
import pers.jie.karate.generator.GeneratorScript;

public class Main {
    public static void main(String[] args) {
        HandleOperation handleOperation = new HandleOperation();
        GeneratorScript generatorScript = new GeneratorScript(handleOperation);
        GeneratorKarateFile generator = new GeneratorKarateFile(generatorScript);
        generator.convertGherkinToKarate();
    }
}
