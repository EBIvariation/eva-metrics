package uk.ac.ebi.eva.metrics.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MetricUtil {

    public static String createAccessionIdentifier(String study, String assembly, long batchNo) throws JSONException {
        JSONObject identifier = new JSONObject();
        identifier.put("study", study);
        identifier.put("assembly", assembly);
        identifier.put("batch", batchNo);
        return identifier.toString();
    }

    public static String createClusteringIdentifier(String assembly) throws JSONException {
        JSONObject identifier = new JSONObject();
        identifier.put("assembly", assembly);
        return identifier.toString();
    }

}
