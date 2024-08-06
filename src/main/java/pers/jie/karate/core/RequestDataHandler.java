package pers.jie.karate.core;

import io.swagger.v3.oas.models.PathItem;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestDataHandler {
    public Map<String, String> findMatchingRequestData(List<Map<String, String>> requestDataList, String path, PathItem.HttpMethod httpMethod) {
        for (Map<String, String> requestData : requestDataList) {
            String requestDataPath = requestData.get("path");
            String requestDataMethod = requestData.get("method");
            System.out.println(requestDataPath);
            //System.out.println(path);
            if (requestDataPath.equals(path) && requestDataMethod.equals(httpMethod.name())) {
                return requestData;
            } else if (rewriteUrl(requestDataPath).equals(path) && requestDataMethod.equals(httpMethod.name())) {
                return requestData;
            }
        }
        return null;
    }

    private String rewriteUrl(String path) {
        Pattern p = Pattern.compile("^/[^/]+");
        Matcher m = p.matcher(path);
        return m.replaceFirst("");
    }

    private   boolean matchPath(String pattern, String path) {
        // Escape special characters in pattern
        pattern = Pattern.quote(pattern);

        // Convert {parameter} to regex capturing group
        pattern = pattern.replaceAll("\\{([^/]+)}", "(?<$1>[^/]+)");

        // Convert to regex pattern and match
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(path);
        return m.matches();
    }
}
