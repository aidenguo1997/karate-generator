package pers.jie.karate.tag;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.List;

public class GherkinTag {
    public String getGherkinTag(String gherkinContent, PathItem.HttpMethod httpMethod, Operation operation, List<String> errors, String path) {
        if (operation.getSummary() == null) {
            errors.add("Operation summary is null for path: " + path + ", HTTP method: " + httpMethod);
            return null; // Skip this operation if summary is null
        }
        String gherkinTag = getMatchingGherkinTag(gherkinContent, operation.getSummary());
        if (gherkinTag == null) {
            errors.add(String.format("No matching Gherkin tag found for Swagger operation summary '%s'", operation.getSummary()));
            return null; // Skip this operation if no matching Gherkin tag found
        }
        return gherkinTag;
    }

    private String getMatchingGherkinTag(String gherkinContent, String summary) {
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
