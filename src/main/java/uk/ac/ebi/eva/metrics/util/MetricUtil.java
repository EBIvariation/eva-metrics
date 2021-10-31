package uk.ac.ebi.eva.metrics.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

public class MetricUtil {

    public static String createAccessionIdentifier(String assembly, String study) {
        try {
            JSONObject identifier = new JSONObject();
            identifier.put("assembly", assembly);
            identifier.put("study", study);
            return identifier.toString();
        } catch (JSONException jsonException) {
            throw new RuntimeException("Could not create Identifier for Accessioning Counts. Error ", jsonException);
        }
    }

    public static String createClusteringIdentifier(String assembly) {
        try {
            JSONObject identifier = new JSONObject();
            identifier.put("assembly", assembly);
            return identifier.toString();
        } catch (JSONException jsonException) {
            throw new RuntimeException("Could not create Identifier for Clustering Counts. Error ", jsonException);
        }
    }

    public static RestTemplate getRestTemplate(CountServiceParameters countServiceParameters) {
        return new RestTemplateBuilder().basicAuthentication(countServiceParameters.getUserName(), countServiceParameters.getPassword()).build();
    }
}
