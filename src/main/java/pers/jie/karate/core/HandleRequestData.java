package pers.jie.karate.core;

import java.util.List;
import java.util.Map;

public class HandleRequestData {
    public Map<String, String> findMatchingRequestData(List<Map<String, String>> requestDataList, String path) {
        for (Map<String, String> requestData : requestDataList) {
            String requestDataPath = requestData.get("path");
            String normalizedRequestPath = requestDataPath.replaceAll(".*/", "").toLowerCase();
            String normalizedPath = path.replaceAll(".*/", "").toLowerCase();
            if (normalizedRequestPath.equals(normalizedPath)) {
                return requestData;
            }
        }
        return null;
    }
}
