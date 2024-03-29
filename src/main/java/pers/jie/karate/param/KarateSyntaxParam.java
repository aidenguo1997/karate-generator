package pers.jie.karate.param;

public class KarateSyntaxParam {
    public static final String SESSION_TAG = "#session";
    public static final String TAG = "#";
    public static final String BACKGROUND_TITLE = "    * ";
    public static final String SCENARIO = "\n  Scenario: %s\n";
    public static final String PROMPT = "    # 依據Karate規範，後續Scenario如需session，則Scenario: %s將移入Background\n"    ;
    public static final String GIVEN = "    Given ";
    public static final String AND = "    And ";
    public static final String WHEN = "    When ";
    public static final String THEN = "    Then ";
    public static final String FEATURE = "Feature: %s\n";
    public static final String BACKGROUND = "  Background:\n";
    public static final String URL = "url '%s'\n";
    public static final String PATH = "path '%s'\n";
    public static final String PARAM = "param %s = '%s'\n";
    public static final String REQUEST = "request %s\n";
    public static final String METHOD = "method %s\n";
    public static final String STATUS = "status %s\n";
    public static final String DEF_TOKEN = "def token = responseHeaders.token\n";
    public static final String HEADER_TOKEN = "header Authorization = token\n";
}
