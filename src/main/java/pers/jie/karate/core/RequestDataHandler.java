package pers.jie.karate.core;

import io.swagger.v3.oas.models.PathItem;

import java.util.List;
import java.util.Map;

public class RequestDataHandler {
    public Map<String, String> findMatchingRequestData(List<Map<String, String>> requestDataList, String path, PathItem.HttpMethod httpMethod) {
        for (Map<String, String> requestData : requestDataList) {
            String requestDataPath = requestData.get("path");
            String requestDataMethod = requestData.get("method");
            String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
            String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
            if (normalizedRequestPath.equals(normalizedPath) && requestDataMethod.equals(httpMethod.name())) {
                return requestData;
            }
        }
        return null;
    }
}
