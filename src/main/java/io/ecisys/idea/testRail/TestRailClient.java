package io.ecisys.idea.testRail;

import io.ecisys.idea.testRail.api.URI;
import io.ecisys.idea.testRail.api.APIClient;
import io.ecisys.idea.testRail.api.APIException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestRailClient {

    private static final String USERNAME = "qa@ecisys.com";
    private static final String AUTH_KEY = "iOVy0WM4jhQOT0N.cBm0-ybQX/7k1nOBynCAQD99B";
    private static final String SERVER_URL = "https://techcase1.translations.com/";

    private static APIClient apiClient;

    public static synchronized APIClient getClient() {
        try {
            if (apiClient.equals(null)) {
                apiClient = new APIClient(SERVER_URL);
            }
        } catch (Exception e) {
            apiClient = new APIClient(SERVER_URL);
        }
        apiClient.setUser(USERNAME);
        apiClient.setPassword(AUTH_KEY);
        return apiClient;
    }

    private static JSONArray getJSONArrayBySendGet(String uri){
        try {
            return  (JSONArray) getClient().sendGet(uri);
        } catch (IOException | APIException e) {

        }
        return null;
    }

    public static String getCaseId(String caseName) {
        JSONArray tests = getJSONArrayBySendGet(URI.GET_CASES);
        if (tests == null){
            return null;
        } else {
            Object obj = tests.stream()
                    .filter(item -> ((JSONObject) item).get(TestRailConstant.TEST_NAME).equals(caseName))
                    .findAny()
                    .orElse(null);
            return obj == null ? null : ((JSONObject) obj).get(TestRailConstant.ID).toString();
        }
    }

    public static boolean updateAnnotationRefField(String caseId, String newValue){

        Map<String, String> data = new HashMap<>();

        data.put(TestRailConstant.CUSTOM_TI_AUTOMATION_REF, newValue);

        try {
            getClient().sendPost(URI.UPDATE_CASE + caseId, data);
            return true;
        } catch (IOException | APIException e) {

        }
        return false;
    }



}
