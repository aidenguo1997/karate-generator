package pers.jie.karate.generator;

public class GherkinTag {
    public String getMatchingGherkinTag(String gherkinContent, String summary) {
        String[] lines = gherkinContent.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("#") && trimmedLine.contains(summary)) {
                return trimmedLine.trim();
            }
        }
        return null;
    }
}
