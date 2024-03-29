package pers.jie.karate.tag;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import pers.jie.karate.param.KarateSyntaxParam;

import java.util.List;
import java.util.Optional;

public class GherkinTag {

    public boolean isSessionTag(String gherkinContent, PathItem.HttpMethod httpMethod, Operation operation, List<String> errors, String path) {
        return getGherkinTag(gherkinContent, httpMethod, operation, errors, path)
                .map(tag -> tag.equals(KarateSyntaxParam.SESSION_TAG))
                .orElse(false);
    }

    public Optional<String> getGherkinTag(String gherkinContent, PathItem.HttpMethod httpMethod, Operation operation, List<String> errors, String path) {
        if (operation.getSummary() == null) {
            errors.add("Operation summary is null for path: " + path + ", HTTP method: " + httpMethod);
        }
        Optional<String> gherkinTag = getMatchingGherkinTag(gherkinContent, operation.getSummary());
        if (gherkinTag.isEmpty()) {
            errors.add(String.format("No matching Gherkin tag found for Swagger operation summary '%s'", operation.getSummary()));
        }
        return gherkinTag   ;
    }

    private Optional<String> getMatchingGherkinTag(String gherkinContent, String summary) {
        if (summary == null) {
            return Optional.empty();
        }
        String[] lines = gherkinContent.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith(KarateSyntaxParam.TAG) && trimmedLine.equals(summary)) {
                return Optional.of(trimmedLine);
            }
        }
        return Optional.empty();
    }
}
