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
import java.util.List;

public abstract class BaseMetricCompute implements MetricCompute {
    private static final Logger logger = LoggerFactory.getLogger(uk.ac.ebi.eva.metrics.metric.MetricCompute.class);
    private static final String URL_PATH_SAVE_COUNT = "/v1/bulk/count";

    private CountServiceParameters countServiceParameters;
    private RestTemplate restTemplate;

    public BaseMetricCompute(CountServiceParameters countServiceParameters, RestTemplate restTemplate) {
        this.countServiceParameters = countServiceParameters;
        this.restTemplate = restTemplate;
    }

    public void clearCount(Metric metric) {
        metric.clearCount();
    }

    public void clearCount() {
        for (Metric metric : getMetrics()) {
            metric.clearCount();
        }
    }

    public void saveMetricsCountsInDB() {
        String processName = getProcessName();
        String identifier = getIdentifier();

        List<Count> counts = new ArrayList<>();
        for (Metric metric : getMetrics()) {
            counts.add(new Count(processName, identifier, metric.getName(), metric.getCount()));
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
