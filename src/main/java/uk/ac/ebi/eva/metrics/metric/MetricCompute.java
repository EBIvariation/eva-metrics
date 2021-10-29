package uk.ac.ebi.eva.metrics.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.Count;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class MetricCompute<T extends Metric> {
    private static final Logger logger = LoggerFactory.getLogger(MetricCompute.class);
    private static final String URL_PATH_SAVE_COUNT = "/v1/bulk/count";

    private RestTemplate restTemplate;
    private CountServiceParameters countServiceParameters;
    private Class computeClass;

    public MetricCompute(RestTemplate restTemplate, CountServiceParameters countServiceParameters, Class computeClass) {
        this.restTemplate = restTemplate;
        this.countServiceParameters = countServiceParameters;
        this.computeClass = computeClass;
    }

    public void addCount(T metric, long count) {
        metric.addCount(count);
    }

    public void saveMetricsCountsInDB(String identifier) {
        List<Count> counts = new ArrayList<>();
        for (T metric : (Iterable<T>) EnumSet.allOf(computeClass)) {
            counts.add(new Count(metric.getProcessName(), identifier, metric.getName(), metric.getCount()));
        }

        HttpHeaders headers;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String url = countServiceParameters.getUrl() + URL_PATH_SAVE_COUNT;
        HttpEntity<Object> requestEntity = new HttpEntity<>(counts, headers);
        ResponseEntity<List<Count>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<List<Count>>() {
                });

        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Metric Count successfully saved In DB");
        } else {
            throw new RestClientException("Could not save count In DB. HttpStatus code is " + response.getStatusCode());
        }
    }
}
